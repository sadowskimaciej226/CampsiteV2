package pl.sadowski.bookingservice.reservation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.sadowski.bookingservice.reservation.exceptions.AccommodationNotFoundException;
import pl.sadowski.bookingservice.reservation.exceptions.ReservationNotFoundException;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationDepartedDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationEvent;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class ReservationService {

    public static final String RESERVATIONS_TOPIC = "reservations";
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Reservation createReservation(String userId, Sector sector, Integer electricBoxNum) {
        log.debug("Start to create reservation for sector: {}", sector);
        Reservation reservation = new Reservation(userId, sector, electricBoxNum);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Accommodation addAccommodation(AccommodationCreationDto dto) {
        Reservation reservation = reservationRepository.findById(dto.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + dto.reservationId()));
        Accommodation accommodation = createAccommodation(dto, reservation);
        reservationRepository.save(reservation);
        return accommodation;
    }


    /**
     * Main responsibility of this method is to finish current state of reservation by changing accommodation. <br>
     * It is done by closing departure time of one accommodation and creating another one even if the next one will have
     * 0 people in accommodation.
    */
    @Transactional
    public Accommodation finishAccommodation(AccommodationDepartedDto depart) {
        Reservation reservation = reservationRepository.findById(depart.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + depart.reservationId()));
        Accommodation accommodation = accommodationRepository.findById(depart.accommodationId())
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found: " + depart.accommodationId()));

        accommodation.completeDepartureWhen(depart.departureTime());

        AccommodationEvent accommodationDepartedEvent = EventBuilder
                .buildDepartedEvent(depart, accommodation.getType(), reservation);

        kafkaTemplate.send(RESERVATIONS_TOPIC, depart.reservationId(), accommodationDepartedEvent);

        int peopleLeft = accommodation.getPeopleLeft(depart.peopleToLeave());
        if (peopleLeft < 0) {
            throw new NoOneOneToDepartException("peopleToLeave exceeds current peopleCount");
        }
        AccommodationCreationDto accommodationCreationDto =
                new AccommodationCreationDto(depart.reservationId(),
                        accommodation.getType(), depart.newAccommodationDescription(),
                        Instant.now(),
                        peopleLeft,
                        reservation.getUserId());
        return createAccommodation(accommodationCreationDto, reservation);
    }

    private Accommodation createAccommodation(AccommodationCreationDto dto, Reservation reservation) {
        Accommodation accommodation = new Accommodation(
                dto.type(),
                dto.description(),
                dto.arrivedAt(),
                dto.peopleCount()
        );
        reservation.addAccommodation(accommodation);
        AccommodationEvent accommodationCreatedEvent
                = EventBuilder.buildAccommodationEvent(dto, reservation);

        kafkaTemplate.send(RESERVATIONS_TOPIC, dto.reservationId(), accommodationCreatedEvent);
        return accommodation;
    }

    @Transactional
    public void completeDeparture(String reservationId, Instant departedAt) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        reservation.completeDeparture(departedAt);
        reservationRepository.save(reservation);
    }

    public boolean isPresent(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        return reservation.getAccommodations().stream()
                .anyMatch(a -> a.getDepartedAt() == null && a.getPeopleCount() > 0);
    }

    public List<Accommodation> getAccommodationHistory(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        return reservation.getAccommodations();
    }

}

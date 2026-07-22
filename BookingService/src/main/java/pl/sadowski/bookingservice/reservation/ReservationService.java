package pl.sadowski.bookingservice.reservation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.sadowski.bookingservice.reservation.exceptions.AccommodationNotFoundException;
import pl.sadowski.bookingservice.reservation.exceptions.ReservationNotFoundException;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreatedDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationDepartedDto;
import pl.sadowski.sdk.avro.AccommodationEvent;

import java.time.Instant;
import java.util.List;

import static pl.sadowski.utils.Topics.RESERVATIONS_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
class ReservationService {

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
    public AccommodationCreatedDto addAccommodation(@Validated AccommodationCreationDto dto) {
        Reservation reservation = reservationRepository.findById(dto.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + dto.reservationId()));
        Accommodation accommodation = new Accommodation(
                dto.type(),
                dto.description(),
                dto.arrivedAt(),
                dto.amount(),
                reservation
        );
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        AccommodationEvent accommodationCreatedEvent
                = EventBuilder.buildAccommodationEvent(savedAccommodation, reservation);

        kafkaTemplate.send(RESERVATIONS_TOPIC, dto.reservationId(), accommodationCreatedEvent);
        return Mapper.mapToDto(savedAccommodation);
    }


    /**
     * Main responsibility of this method is to finish current state of reservation by changing accommodation. <br>
     * It is done by closing departure time of one accommodation and creating another one even if the next one will have
     * 0 people in accommodation.
    */
    @Transactional
    public AccommodationCreatedDto finishAccommodationAndCreateNextOne(@Validated AccommodationDepartedDto depart) {
        Reservation reservation = reservationRepository.findById(depart.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + depart.reservationId()));
        Accommodation accommodationToEnd =
                accommodationRepository.findById(depart.accommodationId())
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found: " + depart.accommodationId()));

        Accommodation newAccommodation = reservation.finishAccommodation(accommodationToEnd, depart.departureTime(),
                depart.amount(), accommodationToEnd.getType(), depart.newAccommodationDescription());

        if (newAccommodation.getAmount() > 0) {
            accommodationRepository.save(newAccommodation);
            sendAccommodationCreatedEvent(depart.reservationId(), newAccommodation, reservation);
        }

        AccommodationEvent accommodationDepartedEvent = EventBuilder
                .buildAccommodationEvent(accommodationToEnd, reservation);
        kafkaTemplate.send(RESERVATIONS_TOPIC, depart.reservationId(), accommodationDepartedEvent);

        return Mapper.mapToDto(newAccommodation);
    }

    private void sendAccommodationCreatedEvent(String reservationId, Accommodation accommodation, Reservation reservation) {
        AccommodationEvent accommodationCreatedEvent
                = EventBuilder.buildAccommodationEvent(accommodation, reservation);
        kafkaTemplate.send(RESERVATIONS_TOPIC, reservation.getId(), accommodationCreatedEvent);
    }

    private Accommodation createAccommodation(AccommodationCreationDto dto, Reservation reservation) {
        Accommodation accommodation = new Accommodation(
                dto.type(),
                dto.description(),
                dto.arrivedAt(),
                dto.amount(),
                reservation
        );

        AccommodationEvent accommodationCreatedEvent
                = EventBuilder.buildAccommodationEvent(accommodation, reservation);

        kafkaTemplate.send(RESERVATIONS_TOPIC, dto.reservationId(), accommodationCreatedEvent);
        return accommodation;
    }

    @Transactional
    public void completeDeparture(String reservationId, Instant departedAt) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

//        reservation.completeDeparture(departedAt);
        reservationRepository.save(reservation);
    }

    public boolean isPresent(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        return reservation.getAccommodations().stream()
                .anyMatch(a -> a.getDepartedAt() == null && a.getAmount() > 0);
    }

    public List<Accommodation> getAccommodationHistory(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        return reservation.getAccommodations();
    }

}

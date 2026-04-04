package pl.sadowski.bookingservice.reservation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationDepartedDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationEvent;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
class ReservationService {

    private final ReservationRepository reservationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Reservation createReservation(String userId, String sector, Integer electricBoxNum) {
        log.debug("Start to create reservation for sector: {}", sector);
        Reservation reservation = new Reservation(userId, sector, electricBoxNum);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Accommodation addAccommodation(String reservationId, AccommodationCreationDto dto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        Accommodation accommodation = new Accommodation(
                dto.type(),
                dto.description(),
                dto.arrivedAt(),
                dto.peopleCount()
        );

        reservation.addAccommodation(accommodation);
        AccommodationEvent accommodationCreatedEvent = EventBuilder.buildAccommodationEvent(reservationId, dto, reservation);

        kafkaTemplate.send("reservations", reservationId, accommodationCreatedEvent);
        reservationRepository.save(reservation);
        return accommodation;
    }

    @Transactional
    public Accommodation finishAccommodation(AccommodationDepartedDto depart) {
        Reservation reservation = reservationRepository.findById(depart.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + depart.reservationId()));

        //It's not expected that reservation has over few accommodations, therefore n + 1 is not scary
        Accommodation accommodation = reservation.getAccommodations().stream()
                .filter(a -> Objects.equals(a.getId(), depart.accommodationId()))
                .findAny().orElseThrow(RuntimeException::new);

        accommodation.completeDepartureWhen(depart.departureTime());

        AccommodationEvent accommodationDepartedEvent = EventBuilder
                .buildDepartedEvent(depart.reservationId(), depart.peopleToLeave(), accommodation.getType(), depart.departureTime(), reservation);

        kafkaTemplate.send("reservations", depart.reservationId(), accommodationDepartedEvent);

        int peopleLeft = accommodation.getPeopleCount() - depart.peopleToLeave();
        if (peopleLeft < 0) {
            throw new IllegalArgumentException("peopleToLeave exceeds current peopleCount");
        }
        AccommodationCreationDto accommodationCreationDto =
                new AccommodationCreationDto(depart.reservationId(), accommodation.getType(), depart.newAccommodationDescription(), Instant.now(), peopleLeft, reservation.getUserId());
        return addAccommodation(depart.reservationId(), accommodationCreationDto);
    }

    @Transactional
    public List<Accommodation> addAccommodations(String reservationId, List<AccommodationCreationDto> dtos) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        List<Accommodation> accommodations = dtos.stream()
                .map(dto -> new Accommodation(
                        dto.type(),
                        dto.description(),
                        dto.arrivedAt(),
                        dto.peopleCount()
                ))
                .toList();

        reservation.addAccommodations(accommodations);
        reservationRepository.save(reservation);

        return accommodations;
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

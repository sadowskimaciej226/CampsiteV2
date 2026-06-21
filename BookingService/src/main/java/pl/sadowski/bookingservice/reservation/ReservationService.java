package pl.sadowski.bookingservice.reservation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.sadowski.bookingservice.reservation.exceptions.AccommodationNotFoundException;
import pl.sadowski.bookingservice.reservation.exceptions.ReservationNotFoundException;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationDepartedDto;
import pl.sadowski.sdk.avro.AccommodationEvent;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
    public Accommodation addAccommodation(AccommodationCreationDto dto) {
        Reservation reservation = reservationRepository.findById(dto.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + dto.reservationId()));
        Accommodation accommodation = createAccommodation(dto, reservation);
        accommodationRepository.save(accommodation);
        return accommodation;
    }


    /**
     * Main responsibility of this method is to finish current state of reservation by changing accommodation. <br>
     * It is done by closing departure time of one accommodation and creating another one even if the next one will have
     * 0 people in accommodation.
    */
    @Transactional
    public Accommodation finishAccommodationAndCreateNextOne(@Validated AccommodationDepartedDto depart) {
        Reservation reservation = reservationRepository.findById(depart.reservationId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + depart.reservationId()));
        Accommodation accommodation =
                accommodationRepository.findById(depart.accommodationId())
                .orElseThrow(() -> new AccommodationNotFoundException("Accommodation not found: " + depart.accommodationId()));

        Accommodation nextAccommodation = reservation.finishAccommodation(accommodation, depart.departureTime(),
                depart.amount(), accommodation.getType(), depart.newAccommodationDescription());

        if (nextAccommodation.getAmount() > 0) {
            accommodationRepository.save(nextAccommodation);
            sendAccommodationCreatedEvent(depart, nextAccommodation, reservation);
        }

        AccommodationEvent accommodationDepartedEvent = EventBuilder
                .buildAccommodationEvent(accommodation, reservation);
        kafkaTemplate.send(RESERVATIONS_TOPIC, depart.reservationId(), accommodationDepartedEvent);

        return nextAccommodation;
    }

    private void sendAccommodationCreatedEvent(AccommodationDepartedDto depart, Accommodation accommodation, Reservation reservation) {
        AccommodationCreationDto accommodationCreationDto = getAccommodationCreationDto(depart, accommodation, reservation);

        AccommodationEvent accommodationCreatedEvent
                = EventBuilder.buildAccommodationEvent(accommodationCreationDto, reservation);

        kafkaTemplate.send(RESERVATIONS_TOPIC, accommodationCreationDto.reservationId(), accommodationCreatedEvent);
    }

    private static @NonNull AccommodationCreationDto getAccommodationCreationDto(AccommodationDepartedDto depart,
                                                                                 Accommodation accommodation,
                                                                                 Reservation reservation) {
        return new AccommodationCreationDto(depart.reservationId(),
                        accommodation.getId(),
                        accommodation.getType(),
                        depart.newAccommodationDescription(),
                        LocalDate.now(),
                        null,
                        depart.amount(),
                        reservation.getUserId());
    }


    private Accommodation createAccommodation(AccommodationCreationDto dto, Reservation reservation) {
        Accommodation accommodation = new Accommodation(
                UUID.randomUUID().toString(),
                dto.type(),
                dto.description(),
                dto.arrivedAt(),
                dto.amount(),
                reservation
        );

        AccommodationEvent accommodationCreatedEvent
                = EventBuilder.buildAccommodationEvent(dto, reservation);

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

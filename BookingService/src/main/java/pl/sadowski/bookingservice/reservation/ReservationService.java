package pl.sadowski.bookingservice.reservation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
class ReservationService {

    private final ReservationRepository reservationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Reservation createReservation(Long userId, String sector, Integer electricBoxNum) {
        Reservation reservation = new Reservation(userId, sector, electricBoxNum);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Accommodation addAccommodation(String reservationId, AccommodationDto dto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        Accommodation accommodation = new Accommodation(
                dto.type(),
                dto.description(),
                dto.arrivedAt(),
                dto.peopleCount()
        );

        reservation.addAccommodation(accommodation);

        kafkaTemplate.send(reservationId, accommodation);

        reservationRepository.save(reservation);
        return accommodation;
    }

    @Transactional
    public List<Accommodation> addAccommodations(String reservationId, List<AccommodationDto> dtos) {
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
    public void completeDeparture(String reservationId, LocalDateTime departedAt) {
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

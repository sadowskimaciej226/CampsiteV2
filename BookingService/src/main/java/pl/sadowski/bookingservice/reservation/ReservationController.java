package pl.sadowski.bookingservice.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/reservation")
@RequiredArgsConstructor
class ReservationController {

    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequestDto reservation) {
        Reservation createdReservation =
                reservationService.createReservation(reservation.userId(), reservation.sector(), reservation.electricBoxNum());
        return ResponseEntity.ok(new ReservationResponse(createdReservation.getId(),
                createdReservation.getUserId(),
                createdReservation.getSector(),
                createdReservation.getElectricBoxNum()));
    }

    @PostMapping("/accommodation")
    public ResponseEntity<?> createAccommodation(@RequestBody AccommodationDto accommodation) {
        reservationService.addAccommodation(accommodation.reservationId(), accommodation);
        return ResponseEntity.ok().build();
    }

}

package pl.sadowski.bookingservice.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationDepartedDto;
import pl.sadowski.bookingservice.reservation.view.ReservationRequestDto;
import pl.sadowski.bookingservice.reservation.view.ReservationResponseDto;

@RestController()
@RequestMapping("/reservation")
@RequiredArgsConstructor
class ReservationController {

    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto reservation) {
        Reservation createdReservation =
                reservationService.createReservation(reservation.userId(), reservation.sector(), reservation.electricBoxNum());
        return ResponseEntity.ok(new ReservationResponseDto(createdReservation.getId(),
                createdReservation.getUserId(),
                createdReservation.getSector(),
                createdReservation.getElectricBoxNum()));
    }

    @PostMapping("/accommodation")
    public ResponseEntity<?> createAccommodation(@RequestBody AccommodationCreationDto accommodation) {
        reservationService.addAccommodation(accommodation.reservationId(), accommodation);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accommodation/departure")
    public ResponseEntity<?> depart(@RequestBody AccommodationDepartedDto accommodationDepartedDto) {
        reservationService.finishAccommodation(accommodationDepartedDto);
        return ResponseEntity.ok().build();
    }

}

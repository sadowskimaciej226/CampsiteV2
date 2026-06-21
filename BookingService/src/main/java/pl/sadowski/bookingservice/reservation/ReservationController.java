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
                reservationService.createReservation(reservation.userId(), Sector.valueOf(reservation.sector()), reservation.electricBoxNum());
        return ResponseEntity.ok(new ReservationResponseDto(createdReservation.getId(),
                createdReservation.getUserId(),
                createdReservation.getSector().toString(),
                createdReservation.getElectricBoxNum()));
    }

    @PostMapping("/accommodation")
    public ResponseEntity<AccommodationCreationDto> createAccommodation(@RequestBody AccommodationCreationDto accommodation) {
        return ResponseEntity.ok(reservationService.addAccommodation(accommodation));
    }

    @PostMapping("/departure")
    public ResponseEntity<AccommodationCreationDto> depart(@RequestBody AccommodationDepartedDto accommodationDepartedDto) {
        return ResponseEntity.ok(reservationService
                .finishAccommodationAndCreateNextOne(accommodationDepartedDto));
    }

}

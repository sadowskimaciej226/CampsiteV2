package pl.sadowski.bookingservice.reservation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.sadowski.bookingservice.reservation.view.*;

@RestController()
@RequestMapping("/reservation")
@RequiredArgsConstructor
class ReservationController {

    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody @Valid ReservationRequestDto reservation) {
        Reservation createdReservation =
                reservationService.createReservation(reservation.userId(), Sector.valueOf(reservation.sector()), reservation.electricBoxNum());
        return ResponseEntity.status(201).body(new ReservationResponseDto(createdReservation.getId(),
                createdReservation.getUserId(),
                createdReservation.getSector().toString(),
                createdReservation.getElectricBoxNum()));
    }

    @PostMapping("/accommodation")
    public ResponseEntity<AccommodationCreatedDto> createAccommodation(@RequestBody @Valid AccommodationCreationDto accommodation) {
        return ResponseEntity.status(201)
                .body(reservationService.addAccommodation(accommodation));
    }

    @PostMapping("/departure")
    public ResponseEntity<AccommodationCreatedDto> depart(@RequestBody @Valid AccommodationDepartedDto accommodationDepartedDto) {
        return ResponseEntity.status(200).body(reservationService
                .finishAccommodationAndCreateNextOne(accommodationDepartedDto));
    }

}

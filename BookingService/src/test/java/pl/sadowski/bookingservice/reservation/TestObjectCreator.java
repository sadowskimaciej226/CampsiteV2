package pl.sadowski.bookingservice.reservation;

import org.jspecify.annotations.NonNull;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationDepartedDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationType;

import java.time.LocalDate;

class TestObjectCreator {

    static @NonNull Reservation createReservation() {
        return Reservation.builder()
                .userId("userId")
                .sector(Sector.A)
                .electricBoxNum(1)
                .present(true)
                .build();
    }

    static @NonNull AccommodationCreationDto createAccommodationCreationDto(String reservationId) {
        return new AccommodationCreationDto(reservationId,
                AccommodationType.CAR, "description", LocalDate.of(2026, 5, 10),
                LocalDate.of(2026, 5, 11), 1, "clientId");
    }

    static @NonNull Accommodation createAccommodation(Reservation reservation) {
        return Accommodation.builder()
                .type(AccommodationType.CAR)
                .description("description")
                .arrivedAt(LocalDate.of(2026, 5, 10))
                .amount(2)
                .reservation(reservation)
                .build();
    }

    static @NonNull AccommodationDepartedDto createAccommodationDepartedDto(Reservation reservation, Accommodation oldAccommodation, int amount) {
        return new AccommodationDepartedDto(reservation.getId(), oldAccommodation.getId(),
                LocalDate.of(2026, 5, 11), amount, "clientId", "newAccommodationDescription");
    }

}

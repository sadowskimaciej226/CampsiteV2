package pl.sadowski.bookingservice.reservation.view;

import java.time.LocalDate;

public record AccommodationDepartedDto(String reservationId,
                                       String accommodationId,
                                       LocalDate departureTime,
                                       int peopleToLeave,
                                       String clientId,
                                       String newAccommodationDescription) {
}

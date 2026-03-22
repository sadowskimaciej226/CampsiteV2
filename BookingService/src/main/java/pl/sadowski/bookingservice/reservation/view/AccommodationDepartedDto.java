package pl.sadowski.bookingservice.reservation.view;

import java.time.Instant;

public record AccommodationDepartedDto(String reservationId,
                                       String accommodationId,
                                       Instant departureTime,
                                       int peopleToLeave,
                                       String clientId,
                                       String newAccommodationDescription) {
}

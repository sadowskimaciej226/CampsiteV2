package pl.sadowski.bookingservice.reservation.view;

import java.time.Instant;

public record AccommodationEvent(String reservationId,
                          int amountOfPeople,
                          String sector,
                          String accommodationType,
                          Instant arrivedAt,
                          Instant departedAt,
                          int electricityBoxNumber) {
}

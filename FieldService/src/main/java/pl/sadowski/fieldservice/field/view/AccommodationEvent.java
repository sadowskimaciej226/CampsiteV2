package pl.sadowski.fieldservice.field.view;

import java.time.Instant;

public record AccommodationEvent(AccommodationEventType eventType,
                                 String reservationId,
                                 int amountOfPeople,
                                 String sector,
                                 String accommodationType,
                                 Instant arrivedAt,
                                 Instant departedAt,
                                 int electricityBoxNumber) {
}

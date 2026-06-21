package pl.sadowski.bookingservice.reservation.view;

import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;

public record AccommodationDepartedDto(@UUID String reservationId,
                                       @UUID String accommodationId,
                                       LocalDate departureTime,
                                       @Min(0)
                                       int amount,
                                       String clientId,
                                       String newAccommodationDescription) {
}

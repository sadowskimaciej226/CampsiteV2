package pl.sadowski.bookingservice.reservation.view;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;

public record AccommodationDepartedDto(@UUID String reservationId,
                                       @UUID String accommodationId,
                                       @NotNull LocalDate departureTime,
                                       @Min(1) int amount,
                                       String clientId,
                                       String newAccommodationDescription) {
}

package pl.sadowski.bookingservice.reservation.view;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AccommodationCreatedDto(@NotBlank String reservationId,
                                       @NotBlank String accommodationId,
                                       @NotNull AccommodationType type,
                                       String description,
                                       @NotNull LocalDate arrivedAt,
                                       LocalDate departedAt,
                                       @Min(1) int amount,
                                       String clientId) {}

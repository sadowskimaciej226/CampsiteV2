package pl.sadowski.bookingservice.reservation.view;

import java.time.LocalDate;

public record AccommodationCreationDto(String reservationId,
                                       String accommodationId,
                                       AccommodationType type,
                                       String description,
                                       LocalDate arrivedAt,
                                       LocalDate departedAt,
                                       int amount,
                                       String clientId) {}

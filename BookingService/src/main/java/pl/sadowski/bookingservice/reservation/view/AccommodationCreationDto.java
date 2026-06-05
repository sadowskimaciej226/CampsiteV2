package pl.sadowski.bookingservice.reservation.view;

import java.time.LocalDate;

public record AccommodationCreationDto(String reservationId,
                                       AccommodationType type,
                                       String description,
                                       LocalDate arrivedAt,
                                       int peopleCount,
                                       String clientId) {}

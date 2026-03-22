package pl.sadowski.bookingservice.reservation.view;


import java.time.Instant;

public record AccommodationCreationDto(String reservationId,
                                       AccommodationType type,
                                       String description,
                                       Instant arrivedAt,
                                       int peopleCount,
                                       String clientId) {}

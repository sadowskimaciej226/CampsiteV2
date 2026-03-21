package pl.sadowski.bookingservice.reservation;


import java.time.LocalDateTime;

record AccommodationDto (String reservationId,
     AccommodationType type,
     String description,
     LocalDateTime arrivedAt,
     int peopleCount,
     String clientId) {}

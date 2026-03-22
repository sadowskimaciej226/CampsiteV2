package pl.sadowski.bookingservice.reservation.view;

public record ReservationResponseDto(String reservationId,
                                     String userId,
                                     String sector,
                                     Integer electricBoxNum) {
}

package pl.sadowski.bookingservice.reservation;

public record ReservationResponse(String reservationId,
                                  Long userId,
                                  String sector,
                                  Integer electricBoxNum) {
}

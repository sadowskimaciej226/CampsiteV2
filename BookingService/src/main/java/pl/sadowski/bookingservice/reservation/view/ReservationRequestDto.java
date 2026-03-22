package pl.sadowski.bookingservice.reservation.view;

public record ReservationRequestDto (String userId,
                              String sector,
                              Integer electricBoxNum){
}

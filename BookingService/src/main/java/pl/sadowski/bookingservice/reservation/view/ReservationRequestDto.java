package pl.sadowski.bookingservice.reservation.view;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationRequestDto (String userId,
                                     @NotNull String sector,
                                     @Min(1) Integer electricBoxNum){
}

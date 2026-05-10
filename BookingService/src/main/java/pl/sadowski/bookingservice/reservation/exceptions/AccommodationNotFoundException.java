package pl.sadowski.bookingservice.reservation.exceptions;

public class AccommodationNotFoundException extends RuntimeException {
    public AccommodationNotFoundException(String message) {
        super(message);
    }
}

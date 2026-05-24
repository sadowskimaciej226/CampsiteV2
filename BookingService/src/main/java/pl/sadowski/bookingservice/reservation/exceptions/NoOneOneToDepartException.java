package pl.sadowski.bookingservice.reservation.exceptions;

public class NoOneOneToDepartException extends RuntimeException {
    public NoOneOneToDepartException(String message) {
        super(message);
    }
}

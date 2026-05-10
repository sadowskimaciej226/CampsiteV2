package pl.sadowski.bookingservice.reservation;

class NoOneOneToDepartException extends RuntimeException {
    public NoOneOneToDepartException(String message) {
        super(message);
    }
}

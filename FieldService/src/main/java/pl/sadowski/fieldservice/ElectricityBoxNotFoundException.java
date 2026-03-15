package pl.sadowski.fieldservice;

public class ElectricityBoxNotFoundException extends RuntimeException {
    public ElectricityBoxNotFoundException(String message) {
        super(message);
    }
}

package pl.sadowski.fieldservice;

public class SectorNotFoundByTag extends RuntimeException {
    public SectorNotFoundByTag(String message) {
        super(message);
    }
}

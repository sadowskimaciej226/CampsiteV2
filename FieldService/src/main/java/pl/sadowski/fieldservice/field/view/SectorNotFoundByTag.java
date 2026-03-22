package pl.sadowski.fieldservice.field.view;

public class SectorNotFoundByTag extends RuntimeException {
    public SectorNotFoundByTag(String message) {
        super(message);
    }
}

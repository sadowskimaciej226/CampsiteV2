package pl.sadowski.fieldservice.field.view;

public record AccommodationEvent (
        String clientId,
        String sectorTag,
        int electricityBox,
        int amountOfPeople
){
}

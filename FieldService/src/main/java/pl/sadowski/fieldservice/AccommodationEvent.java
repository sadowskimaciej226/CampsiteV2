package pl.sadowski.fieldservice;

record AccommodationEvent (
        String clientId,
        String sectorTag,
        int electricityBox,
        int amountOfPeople
){
}

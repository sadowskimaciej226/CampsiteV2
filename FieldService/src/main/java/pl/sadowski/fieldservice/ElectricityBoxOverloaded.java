package pl.sadowski.fieldservice;

public record ElectricityBoxOverloaded(SectorTag sectorTag, int contactsOver, int boxNumber) {
}

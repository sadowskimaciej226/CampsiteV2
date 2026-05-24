package pl.sadowski.fieldservice.field.view;

import lombok.Builder;

import java.util.Map;

@Builder
public record CampsiteReportDto(SectorTag sectorTag,
                                int maxPeople,
                                int occupiedPeople,
                                Map<Integer, Integer> availableContacts) {
}

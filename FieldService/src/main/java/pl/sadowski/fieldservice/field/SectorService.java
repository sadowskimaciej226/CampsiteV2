package pl.sadowski.fieldservice.field;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.sadowski.fieldservice.field.view.CampsiteReportDto;
import pl.sadowski.fieldservice.field.view.SectorTag;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void assignPeople(SectorTag sectorTag, Integer electricityBoxNumber, int amountOfPeople) {

        Sector sector = sectorRepository.findSectorBySectorTag(sectorTag);

        sector.assignToSector(electricityBoxNumber, amountOfPeople);

        sectorRepository.save(sector);

        DomainEvents.drainEvents()
                .forEach(applicationEventPublisher::publishEvent);

    }

    public void releasePeople(SectorTag sectorTag, Integer electricityBoxNumber, int amountOfPeople) {
        Sector sector = sectorRepository.findSectorBySectorTag(sectorTag);

        sector.releasePersonFromSector(electricityBoxNumber, amountOfPeople);

        sectorRepository.save(sector);

        DomainEvents.drainEvents()
                .forEach(applicationEventPublisher::publishEvent);
    }

    public List<CampsiteReportDto> getAllSectorsInfo() {
        return sectorRepository.findAll().stream()
                .map(this::mapToCampsiteReportDto)
                .toList();

    }

    private CampsiteReportDto mapToCampsiteReportDto(Sector sector) {
        return CampsiteReportDto.builder()
                .occupiedPeople(sector.getOccupiedPeople())
                .maxPeople(sector.getMaxPeople())
                .sectorTag(sector.getSectorTag())
                .availableContacts(createContactMap(sector.getElectricityBoxList()))
                .build();
    }

    private Map<Integer, Integer> createContactMap(List<ElectricityBox> electricityBoxList) {
        return electricityBoxList.stream()
                .collect(Collectors.toMap(ElectricityBox::getBoxNumber, ElectricityBox::getFreeElectricContactsAmount));
    }
}

package pl.sadowski.fieldservice;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
}

package pl.sadowski.fieldservice.field;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.sadowski.fieldservice.field.view.AccommodationEvent;
import pl.sadowski.fieldservice.field.view.SectorTag;

@Service
@RequiredArgsConstructor
class SectorEventListener {

    private final SectorService sectorService;

    @KafkaListener(topics = "accomodation-request", groupId = "sector-service")
    public void assignNewClientsToField(AccommodationEvent accommodation) {
        sectorService.assignPeople(SectorTag.valueOf(accommodation.sectorTag()), accommodation.electricityBox(), accommodation.amountOfPeople());
    }
}

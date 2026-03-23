package pl.sadowski.fieldservice.field;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.sadowski.fieldservice.field.view.AccommodationEvent;
import pl.sadowski.fieldservice.field.view.AccommodationEventType;
import pl.sadowski.fieldservice.field.view.SectorTag;

@Service
@RequiredArgsConstructor
@Slf4j
class SectorEventListener {

    private final SectorService sectorService;

    @KafkaListener(topics = "reservations", groupId = "sector-service")
    public void assignNewClientsToField(AccommodationEvent accommodation) {
        if (accommodation.eventType() == AccommodationEventType.DEPARTURE) {
            sectorService.releasePeople(SectorTag.valueOf(accommodation.sector()), accommodation.electricityBoxNumber(), accommodation.amountOfPeople());
        } else if(accommodation.eventType() == AccommodationEventType.ARRIVAL){
            sectorService.assignPeople(SectorTag.valueOf(accommodation.sector()), accommodation.electricityBoxNumber(), accommodation.amountOfPeople());
        }
        else {
            log.error("Unhandled event type");
        }
    }
}

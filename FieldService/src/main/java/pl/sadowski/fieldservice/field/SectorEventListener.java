package pl.sadowski.fieldservice.field;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.sadowski.fieldservice.field.view.SectorTag;
import pl.sadowski.sdk.avro.AccommodationEvent;
import pl.sadowski.sdk.avro.AccommodationEventType;

@Service
@RequiredArgsConstructor
@Slf4j
class SectorEventListener {

    private final SectorService sectorService;

    @KafkaListener(topics = "reservations", groupId = "sector-service")
    public void assignNewClientsToField(AccommodationEvent accommodation) {
        if (accommodation.getEventType() == AccommodationEventType.DEPARTURE) {
            sectorService.releasePeople(SectorTag.valueOf(accommodation.getSector()),
                    accommodation.getElectricityBoxNumber(), accommodation.getAmountOfPeople());
        } else if(accommodation.getEventType() == AccommodationEventType.ARRIVAL){
            sectorService.assignPeople(SectorTag.valueOf(accommodation.getSector()),
                    accommodation.getElectricityBoxNumber(), accommodation.getAmountOfPeople());
        }
        else {
            log.error("Unhandled event type");
        }
    }
}

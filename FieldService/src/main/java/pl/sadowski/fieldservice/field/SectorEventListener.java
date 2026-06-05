package pl.sadowski.fieldservice.field;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.Acknowledgment;
import pl.sadowski.fieldservice.field.view.SectorTag;
import pl.sadowski.sdk.avro.AccommodationEvent;
import pl.sadowski.sdk.avro.AccommodationEventType;

@Service
@RequiredArgsConstructor
@Slf4j
class SectorEventListener {

    private final SectorService sectorService;

    @KafkaListener(topics = "reservations", groupId = "sector-service")
    public void assignNewClientsToField(AccommodationEvent accommodation, Acknowledgment ack) {
        if (accommodation.getEventType() == AccommodationEventType.DEPARTURE) {
            sectorService.releasePeople(SectorTag.valueOf(accommodation.getSector()),
                    accommodation.getElectricityBoxNumber(), accommodation.getAmountOfPeople());
            ack.acknowledge();
        } else if(accommodation.getEventType() == AccommodationEventType.ARRIVAL){
            sectorService.assignPeople(SectorTag.valueOf(accommodation.getSector()),
                    accommodation.getElectricityBoxNumber(), accommodation.getAmountOfPeople());
            ack.acknowledge();
        }
        else {
            log.error("Unhandled event type");
        }
    }
}

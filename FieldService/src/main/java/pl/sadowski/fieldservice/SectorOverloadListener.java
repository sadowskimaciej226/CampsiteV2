package pl.sadowski.fieldservice;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
class SectorOverloadListener {

    @EventListener
    public void handleSectorOverload(SectorOverloaded event) {
        //TODO for now event just logged in future development handle internally to produced reports about overloads or
        //TODO new service have to be introduced
        log.warn("Sector overloaded sector tag: {}, amount overloaded {}", event.sectorTag(), event.peopleOver());
    }

    @EventListener
    public void handleElectricityBoxOverloaded(ElectricityBoxOverloaded event) {
        //TODO for now event just logged in future development handle internally to produced reports about overloads or
        //TODO new service have to be introduced
        log.warn("Electricity box overloaded sector tag: {}, box number: {}, amount overloaded {}",
                event.sectorTag(), event.boxNumber(), event.contactsOver());
    }
}

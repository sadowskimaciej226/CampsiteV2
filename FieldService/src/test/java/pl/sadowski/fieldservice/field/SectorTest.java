package pl.sadowski.fieldservice.field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sadowski.fieldservice.field.view.DomainEvents;
import pl.sadowski.fieldservice.field.view.SectorTag;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SectorTest {
    private Sector sector;
    private ElectricityBox box1;
    private ElectricityBox box2;

    @BeforeEach
    void setUp() {
        box1 = new ElectricityBox(1, 2); // boxNumber=1, maxCapacity=2
        box2 = new ElectricityBox(2, 1); // boxNumber=2, maxCapacity=1
        sector = new Sector(SectorTag.A, 5, List.of(box1, box2));
    }

    @Test
    void shouldAssignPersonToSector_whenCapacityAvailable() {
        sector.assignToSector(1,  2);

        assertThat(sector.getOccupiedPeople()).isEqualTo(2);
        assertThat(box1.getFreeElectricContactsAmount()).isEqualTo(1);
    }

    @Test
    void shouldRaiseOverloadEvent_whenSectorCapacityExceeded() {
        DomainEvents.drainEvents();

        sector.assignToSector(1, 6);

        assertThat(DomainEvents.drainEvents().size()).isEqualTo(1);
    }

    @Test
    void shouldUseElectricContact_whenRequested() {
        sector.assignToSector(1, 1);

        assertThat(box1.getFreeElectricContactsAmount()).isEqualTo(1);
    }

    @Test
    void shouldReleasePersonFromSector() {
        sector.assignToSector(1, 2);
        sector.releasePersonFromSector(1, 1);

        assertThat(sector.getOccupiedPeople()).isEqualTo(1);
        assertThat(box1.getFreeElectricContactsAmount()).isEqualTo(2);
    }
}
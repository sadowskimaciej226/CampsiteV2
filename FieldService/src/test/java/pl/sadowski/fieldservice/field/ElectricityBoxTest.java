package pl.sadowski.fieldservice.field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.sadowski.fieldservice.field.view.DomainEvents;
import pl.sadowski.fieldservice.field.view.SectorTag;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ElectricityBoxTest {

    private Sector sector;
    private ElectricityBox box;

    @BeforeEach
    void setUp() {
        sector = new Sector(SectorTag.A, 5, List.of());
        box = new ElectricityBox(1, 2);
        box.setSector(sector);
    }

    @Test
    void shouldDecreaseFreeContacts_whenUseContact() {
        box.useContact();
        assertThat(box.getFreeElectricContactsAmount()).isEqualTo(1);
    }

    @Test
    void shouldRaiseOverloadEvent_whenNoFreeContacts() {
        DomainEvents.drainEvents();

        box.useContact();
        box.useContact(); // teraz freeElectricContactsAmount = 0
        box.useContact(); // przeciążenie

        assertThat(DomainEvents.drainEvents().size()).isEqualTo(1);
    }

    @Test
    void shouldIncreaseFreeContacts_whenReleaseContact() {
        box.useContact();
        box.releaseContact();

        assertThat(box.getFreeElectricContactsAmount()).isEqualTo(2);
    }
}
package pl.sadowski.fieldservice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
class ElectricityBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer boxNumber;
    private int freeElectricContactsAmount;
    private int positionX;
    private int positionY;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private Sector sector;
    private int maxCapacity;

    public ElectricityBox(Integer boxNumber, int maxCapacity) {
        this.boxNumber = boxNumber;
        this.maxCapacity = maxCapacity;
        this.freeElectricContactsAmount = maxCapacity;
    }

    protected void setSector(Sector sector) { this.sector = sector; }

    public boolean isFreeElectricContact() {
        return freeElectricContactsAmount > 0;
    }

    public void useContact() {
        if(!isFreeElectricContact()) {
            int overloadedContacts = freeElectricContactsAmount - 1;
            DomainEvents.raise(new ElectricityBoxOverloaded(sector.getSectorTag(), overloadedContacts, boxNumber));
        }
        freeElectricContactsAmount--;
    }

    public void releaseContact() {
        freeElectricContactsAmount++;
    }
}

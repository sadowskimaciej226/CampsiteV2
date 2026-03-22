package pl.sadowski.fieldservice.field;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.sadowski.fieldservice.field.view.DomainEvents;
import pl.sadowski.fieldservice.field.view.SectorOverloaded;
import pl.sadowski.fieldservice.field.view.SectorTag;


import java.util.List;

@Entity
@Getter
@NoArgsConstructor
class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private SectorTag sectorTag;
    private int maxPeople;
    private int occupiedPeople;

    @OneToMany(mappedBy = "sector", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ElectricityBox> electricityBoxList;

    public Sector(SectorTag sectorTag, int maxPeople, List<ElectricityBox> electricityBoxList) {
        this.sectorTag = sectorTag;
        this.maxPeople = maxPeople;
        this.occupiedPeople = 0;
        this.electricityBoxList = electricityBoxList;
        this.electricityBoxList.forEach(b -> b.setSector(this));
    }

    public void assignToSector(Integer boxNumber, int amountOfPeople) {
        boolean overloaded = !hasFreeCapacity(amountOfPeople);
        this.occupiedPeople += amountOfPeople;

        if (overloaded) {
            int overloadAmount = Math.max(0, occupiedPeople - maxPeople);
            DomainEvents.raise(new SectorOverloaded(sectorTag, overloadAmount));
        }
        if(boxNumber != null) {
            ElectricityBox electricityBox = getElectricityBox(boxNumber);
            electricityBox.useContact();
        }
    }

    private ElectricityBox getElectricityBox(int boxNumber) {
        return electricityBoxList.stream()
                .filter(box -> box.getBoxNumber() == boxNumber)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No such box number " + boxNumber));
    }

    private boolean hasFreeCapacity(int peopleToJoin) {
        return maxPeople >= peopleToJoin + occupiedPeople;
    }

    public void releasePersonFromSector(Integer electricityBoxNum, int amountOfPeople) {
        occupiedPeople = Math.max(0, occupiedPeople-amountOfPeople);
        if (electricityBoxNum != null) {
            getElectricityBox(electricityBoxNum).releaseContact();
        }
    }
}

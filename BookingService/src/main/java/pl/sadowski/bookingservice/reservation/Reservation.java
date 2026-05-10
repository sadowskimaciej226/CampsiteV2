package pl.sadowski.bookingservice.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.sadowski.bookingservice.reservation.view.AccommodationType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    private String userId;
    @Enumerated(EnumType.STRING)
    private Sector sector;
    private Integer electricBoxNum;
    private boolean present;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "reservation")
    private final List<Accommodation> accommodations = new ArrayList<>();

    Reservation(String userId, Sector sector, Integer electricBoxNum) {
        this.userId = userId;
        this.sector = sector;
        this.electricBoxNum = electricBoxNum;
        this.present = true;
    }

    Accommodation finishAccommodation(Accommodation accommodationToFinish,
                                      Instant departureTime,
                                      int peopleLeaving,
                                      AccommodationType nextType,
                                      String nextDescription) {
        accommodationToFinish.departPeople(peopleLeaving);
        accommodationToFinish.markDepartedAt(departureTime);

        int remaining = accommodationToFinish.getPeopleCount();

        Accommodation next = new Accommodation(nextType, nextDescription, departureTime, remaining, this);

        this.accommodations.add(next);

        return next;
    }

//    void completeDeparture(Instant when) {
//        accommodations.stream()
//                .filter(a -> a.getDepartedAt() == null)
//                .forEach(a -> a.markedDepartedAt(when));
//        present = false;
//
//    }
//
//    void addAccommodation(Accommodation accommodation) {
//        accommodations.add(accommodation);
//    }
//
//    Accommodation finishAccommodation() {
//
//    }

}

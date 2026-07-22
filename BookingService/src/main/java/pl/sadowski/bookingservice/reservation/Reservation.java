package pl.sadowski.bookingservice.reservation;

import jakarta.persistence.*;
import lombok.*;
import pl.sadowski.bookingservice.reservation.view.AccommodationType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
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
                                      LocalDate departureTime,
                                      int amount,
                                      AccommodationType nextType,
                                      String nextDescription) {
        accommodationToFinish.markDepartedAt(departureTime);

        int remaining = accommodationToFinish.getAmount() - amount;

        Accommodation next = new Accommodation(nextType, nextDescription, departureTime, remaining, this);

        this.accommodations.add(next);

        return next;
    }


}

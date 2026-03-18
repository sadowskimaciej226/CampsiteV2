package pl.sadowski.bookingservice.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    private Long userId;
    private String sector;
    private Integer electricBoxNum;
    private boolean present;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reservation_id")
    private final List<Accommodation> accommodations = new ArrayList<>();

    Reservation(Long  userId, String sector, Integer electricBoxNum) {
        this.userId = userId;
        this.sector = sector;
        this.electricBoxNum = electricBoxNum;
        this.present = true;
    }

    void completeDeparture(LocalDateTime when) {
        accommodations.stream()
                .filter(a -> a.getDepartedAt() == null)
                .forEach(a -> a.completeDeparture(when));
        present = false;

    }

    void addAccommodation(Accommodation accommodation) {
        accommodations.add(accommodation);
    }

    void addAccommodations(List<Accommodation> accommodations) {
        this.accommodations.addAll(accommodations);
    }


}

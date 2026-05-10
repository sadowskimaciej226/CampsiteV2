package pl.sadowski.bookingservice.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.sadowski.bookingservice.reservation.view.AccommodationType;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private AccommodationType type;

    private String description;

    private Instant arrivedAt;
    private Instant departedAt;

    private int peopleCount;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;


    Accommodation(AccommodationType type, String description, Instant arrivedAt, int peopleCount, Reservation reservation) {
        this.type = type;
        this.description = description;
        this.arrivedAt = arrivedAt;
        this.peopleCount = peopleCount;
        this.reservation = reservation;
    }

    void departPeople(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Cannot depart negative people");
        }
        if (count > peopleCount) {
            throw new NoOneOneToDepartException("Too many people leaving");
        }
        this.peopleCount -= count;
    }

    void markDepartedAt(Instant when) {
        if (this.departedAt != null) {
            throw new IllegalArgumentException("Already departed");
        }
        this.departedAt = when;
    }
}

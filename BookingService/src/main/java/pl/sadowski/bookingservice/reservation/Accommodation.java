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


    Accommodation(AccommodationType type, String description, Instant arrivedAt, int peopleCount) {
        this.type = type;
        this.description = description;
        this.arrivedAt = arrivedAt;
        this.peopleCount = peopleCount;
    }

    public void completeDepartureWhen(Instant when) {
        this.departedAt = when;
    }

}

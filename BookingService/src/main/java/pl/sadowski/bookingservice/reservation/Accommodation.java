package pl.sadowski.bookingservice.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private LocalDateTime arrivedAt;
    private LocalDateTime departedAt;

    private int peopleCount;


    Accommodation(AccommodationType type, String description, LocalDateTime arrivedAt, int peopleCount) {
        this.type = type;
        this.description = description;
        this.arrivedAt = arrivedAt;
        this.peopleCount = peopleCount;
    }

    public void completeDeparture(LocalDateTime when) {
        this.departedAt = when;
    }

}

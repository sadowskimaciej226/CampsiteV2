package pl.sadowski.bookingservice.reservation;

import jakarta.persistence.*;
import lombok.*;
import pl.sadowski.bookingservice.reservation.view.AccommodationType;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private AccommodationType type;
    private String description;
    private LocalDate arrivedAt;
    private LocalDate departedAt;
    private int amount;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;


    Accommodation(AccommodationType type, String description, LocalDate arrivedAt, int amount, Reservation reservation) {

        this.type = type;
        this.description = description;
        this.arrivedAt = arrivedAt;
        this.amount = amount;
        this.reservation = reservation;
    }

    void markDepartedAt(LocalDate when) {
        if (this.departedAt != null) {
            throw new IllegalArgumentException("Already departed");
        }
        this.departedAt = when;
    }
}

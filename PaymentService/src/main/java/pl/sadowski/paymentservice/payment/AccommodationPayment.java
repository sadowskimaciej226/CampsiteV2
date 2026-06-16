package pl.sadowski.paymentservice.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import pl.sadowski.utils.validation.Arrival;
import pl.sadowski.utils.validation.Departure;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class AccommodationPayment {
    @Id
    @UUID
    private String accommodationId;
    @UUID
    private String reservationId;
    @NotEmpty
    private String sector;
    @Min(0)
    private int amount;
    private String accommodationType;
    @Enumerated(EnumType.STRING)
    private Rule rule;
    private boolean electricityConnected;
    @Arrival
    private LocalDate arrivedAt;
    @Departure
    private LocalDate departedAt;
    private boolean paid;

    AccommodationPayment(AccommodationPayment original,
                         LocalDate newArrivedAt,
                         LocalDate newDepartedAt) {
        this.accommodationId = original.getAccommodationId();
        this.reservationId = original.getReservationId();
        this.sector = original.getSector();
        this.amount = original.getAmount();
        this.accommodationType = original.getAccommodationType();
        this.rule = original.getRule();
        this.electricityConnected = original.isElectricityConnected();
        this.arrivedAt = newArrivedAt;
        this.departedAt = newDepartedAt;
    }

}

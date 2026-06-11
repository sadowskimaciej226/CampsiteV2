package pl.sadowski.paymentservice.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
class PricingRule {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private Rule rule;
    @Enumerated(EnumType.STRING)
    private SeasonType seasonType;
    private BigDecimal price;
    private LocalDate validFrom;
    private LocalDate validTo;
}

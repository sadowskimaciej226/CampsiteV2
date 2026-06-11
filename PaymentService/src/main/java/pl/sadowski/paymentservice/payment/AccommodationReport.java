package pl.sadowski.paymentservice.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
class AccommodationReport {
    private int numberOfDays;
    private BigDecimal costOfAccommodation;
    private PricingRule pricingRule;
}

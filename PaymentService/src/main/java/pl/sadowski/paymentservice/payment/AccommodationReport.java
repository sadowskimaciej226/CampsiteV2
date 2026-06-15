package pl.sadowski.paymentservice.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
class AccommodationReport {
    private int numberOfDays;
    private BigDecimal costOfAccommodation;
    private PricingRule pricingRule;
    private LocalDate from;
    private LocalDate to;

    AccommodationReport(List<PricingRule> pricingsByRule,
                        AccommodationPayment accommodation) {
        log.debug("Accommodation Payment: {}", accommodation);
        PricingRule pricingRule = PricingRuleValidator.getValidPricingRule(accommodation, pricingsByRule);
        int days = (int) ChronoUnit.DAYS.between(accommodation.getArrivedAt(), accommodation.getDepartedAt());
        BigDecimal price = pricingRule.getPrice().multiply(BigDecimal.valueOf(days));
        this.numberOfDays = days;
        this.costOfAccommodation = price;
        this.pricingRule = pricingRule;
        this.from = accommodation.getArrivedAt();
        this.to = accommodation.getDepartedAt();
    }
}

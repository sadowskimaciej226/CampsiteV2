package pl.sadowski.paymentservice.payment;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
class PaymentProcessor {

    public ReservationPaymentReport calculatePayment(List<PricingRule> pricingRules, List<AccommodationPayment> accommodationToBePaid) {
        Map<Rule, List<PricingRule>> rulesByType = pricingRules.stream()
                .collect(Collectors.groupingBy(PricingRule::getRule));

        List<AccommodationReport> accommodationReports = accommodationToBePaid.stream()
                .map(accommodation -> createAccommodationReport(rulesByType, accommodation))
                .toList();

        return new ReservationPaymentReport(accommodationReports);
    }

    private AccommodationReport createAccommodationReport(Map<Rule, List<PricingRule>> rulesByType,
                                                          AccommodationPayment accommodation) {
        log.debug("Accommodation Payment: {}", accommodation);
        List<PricingRule> pricingsByRule = rulesByType.getOrDefault(accommodation.getRule(), Collections.emptyList());
        PricingRule pricingRule = getValidPricingRule(accommodation, pricingsByRule);
        int days = (int) ChronoUnit.DAYS
                .between(accommodation.getArrivedAt(), accommodation.getDepartedAt());
        BigDecimal price = pricingRule.getPrice().multiply(BigDecimal.valueOf(days));
        return new AccommodationReport(days, price, pricingRule);

    }

    private @NonNull PricingRule getValidPricingRule(AccommodationPayment accommodation, List<PricingRule> pricingsByRule) {
        return pricingsByRule.stream()
                .filter(pr -> validateRule(pr, accommodation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No valid pricing rule for accommodation with id: " + accommodation.getAccommodationId()));
    }

    private boolean validateRule(PricingRule pr, AccommodationPayment accommodation) {
        return accommodation.getArrivedAt().isAfter(pr.getValidFrom()) &&
                accommodation.getDepartedAt().isBefore(pr.getValidTo());
    }
}

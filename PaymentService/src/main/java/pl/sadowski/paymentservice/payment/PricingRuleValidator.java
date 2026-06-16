package pl.sadowski.paymentservice.payment;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

import java.util.List;

@UtilityClass
class PricingRuleValidator {

    @NonNull PricingRule getValidPricingRule(AccommodationPayment accommodation, List<PricingRule> pricingsByRule) {
        return pricingsByRule.stream()
                .filter(pr -> validateRule(pr, accommodation))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No valid pricing rule for accommodation with id: " + accommodation.getAccommodationId()));
    }

    private boolean validateRule(PricingRule pr, AccommodationPayment accommodation) {
        return (accommodation.getArrivedAt().isAfter(pr.getValidFrom()) ||
                accommodation.getArrivedAt().isEqual(pr.getValidFrom())) &&
                (accommodation.getArrivedAt().isBefore(pr.getValidTo()) ||
                accommodation.getArrivedAt().isEqual(pr.getValidTo()));
    }
}

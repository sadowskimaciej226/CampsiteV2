package pl.sadowski.paymentservice.payment;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
class PaymentProcessor {

    public ReservationPaymentReport calculatePayment(List<PricingRule> pricingRules, List<AccommodationPayment> accommodationToBePaid) {
        Map<Rule, List<PricingRule>> rulesByType = pricingRules.stream()
                .collect(Collectors.groupingBy(PricingRule::getRule));
        List<AccommodationPayment> validAccommodation = processAccommodationByValidPricingRuleDate(accommodationToBePaid,
                rulesByType);

        List<AccommodationReport> accommodationReports = validAccommodation.stream()
                .map(accommodation -> new AccommodationReport(rulesByType
                        .getOrDefault(accommodation.getRule(), Collections.emptyList()), accommodation))
                .toList();

        return new ReservationPaymentReport(accommodationReports);
    }

    private List<AccommodationPayment> processAccommodationByValidPricingRuleDate(List<AccommodationPayment> accommodationToBePaid,
                                                                                  @MonotonicNonNull Map<Rule, List<PricingRule>> pricingRule) {
        return accommodationToBePaid.stream()
                .filter(accommodationPayment -> !accommodationPayment.isPaid())
                .flatMap(accommodation -> {
                    List<PricingRule> applicableRules = pricingRule
                            .getOrDefault(accommodation.getRule(), Collections.emptyList());
                    if (applicableRules.isEmpty()) {
                        throw new IllegalArgumentException("No pricing rule found for rule: " + accommodation.getRule());
                    }
                    return divideAccommodationByPricingPeriods(accommodation, applicableRules)
                            .stream();
                })
                .toList();
    }

    private List<AccommodationPayment> divideAccommodationByPricingPeriods(AccommodationPayment accommodation,
                                                                           List<PricingRule> pricingRules) {
        List<AccommodationPayment> result = new ArrayList<>();
        LocalDate currentDate = accommodation.getArrivedAt();
        LocalDate departureDate = accommodation.getDepartedAt();

        while (currentDate.isBefore(departureDate)) {
            PricingRule applicableRule = findRuleForDate(pricingRules, currentDate)
                    .orElseThrow(() -> new IllegalArgumentException("No pricing rule found for accommodation: "
                            + accommodation.getAccommodationId()));

            LocalDate periodEnd = departureDate.isBefore(applicableRule.getValidTo())
                    ? departureDate
                    : applicableRule.getValidTo().plusDays(1);

            AccommodationPayment periodAccommodation = new AccommodationPayment(
                    accommodation,
                    currentDate,
                    periodEnd);
            result.add(periodAccommodation);
            currentDate = periodEnd;
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException(
                    "Failed to divide accommodation: " + accommodation.getAccommodationId());
        }

        log.debug("Divided accommodation {} into {} periods",
                accommodation.getAccommodationId(), result.size());

        return result;
    }

    private Optional<PricingRule> findRuleForDate(List<PricingRule> pricingRules, LocalDate date) {
        return pricingRules.stream()
                .filter(pr -> isDateInPricingRange(date, pr))
                .max(Comparator.comparing(PricingRule::getValidFrom));
    }

    private boolean isDateInPricingRange(LocalDate date, PricingRule rule) {
        return !date.isBefore(rule.getValidFrom()) &&
                !date.isAfter(rule.getValidTo());
    }
}

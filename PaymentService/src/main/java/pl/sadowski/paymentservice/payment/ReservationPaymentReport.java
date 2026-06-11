package pl.sadowski.paymentservice.payment;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
class ReservationPaymentReport {
    private final List<AccommodationReport> accommodationReports;
    private final BigDecimal totalCost;

    ReservationPaymentReport(List<AccommodationReport> accommodationReports) {
        this.accommodationReports = accommodationReports;
        this.totalCost = accommodationReports.stream()
                .map(AccommodationReport::getCostOfAccommodation)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

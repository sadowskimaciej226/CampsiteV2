package pl.sadowski.paymentservice.payment;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentProcessorTest {
    PaymentProcessor paymentProcessor = new PaymentProcessor();
    List<PricingRule> pricingRules;
    List<AccommodationPayment> accommodationPayments;

    private static @NonNull List<PricingRule> getPricingRules() {
        return List.of(
                new PricingRule(1L, Rule.TENT, SeasonType.LOW, BigDecimal.valueOf(10L), LocalDate.of(2026, 4, 1), LocalDate.of(2026, 6, 29)),
                new PricingRule(1L, Rule.TENT, SeasonType.HIGH, BigDecimal.valueOf(20L), LocalDate.of(2026, 6, 30), LocalDate.of(2026, 8, 30)),
                new PricingRule(1L, Rule.ADULT, SeasonType.LOW, BigDecimal.valueOf(20L), LocalDate.of(2026, 4, 1), LocalDate.of(2026, 6, 29)),
                new PricingRule(1L, Rule.ADULT, SeasonType.HIGH, BigDecimal.valueOf(25L), LocalDate.of(2026, 6, 30), LocalDate.of(2026, 8, 30)),
                new PricingRule(1L, Rule.CHILD, SeasonType.LOW, BigDecimal.valueOf(20L), LocalDate.of(2026, 4, 1), LocalDate.of(2026, 6, 29)),
                new PricingRule(1L, Rule.CHILD, SeasonType.HIGH, BigDecimal.valueOf(10L), LocalDate.of(2026, 6, 30), LocalDate.of(2026, 8, 30)));
    }

    @Test
    void calculatePaymentsShouldCalculateCostOfAccommodationForOneNightWithMoreThanOneItem() {
        //given
        accommodationPayments =  List.of(
                new AccommodationPayment("accommodationId", "reservationId", "B", 2, "departed", Rule.TENT,
                        true, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 2), false));
        pricingRules = getPricingRules();
        //when
        ReservationPaymentReport reservationPaymentReport = paymentProcessor.calculatePayment(pricingRules, accommodationPayments);
        //then
        // 10 * 2
        assertThat(reservationPaymentReport.getTotalCost(), equalTo(BigDecimal.valueOf(20)));
        List<AccommodationReport> accommodationReports = reservationPaymentReport.getAccommodationReports();
        assertThat(accommodationReports, hasSize(1));
    }

    @Test
    void calculatePaymentsShouldCalculateCostOfAccommodationForMoreThanOneNight() {
        //given
        accommodationPayments =  List.of(
                new AccommodationPayment("accommodationId", "reservationId", "B", 1, "departed", Rule.TENT,
                        true, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 3), false));
        pricingRules = getPricingRules();
        //when
        ReservationPaymentReport reservationPaymentReport = paymentProcessor.calculatePayment(pricingRules, accommodationPayments);
        //then
        // 10 * 2
        assertThat(reservationPaymentReport.getTotalCost(), equalTo(BigDecimal.valueOf(20)));
        List<AccommodationReport> accommodationReports = reservationPaymentReport.getAccommodationReports();
        assertThat(accommodationReports, hasSize(1));
    }

    @Test
    void calculatePaymentsShouldCalculateCostOfAccommodationWhenAccommodationGoesThroughSeasonTypes() {
        //given
        accommodationPayments =  List.of(
                new AccommodationPayment("accommodationId", "reservationId", "B", 1, "departed", Rule.TENT,
                        true, LocalDate.of(2026, 6, 29), LocalDate.of(2026, 7, 1), false));
        pricingRules = getPricingRules();
        //when
        ReservationPaymentReport reservationPaymentReport = paymentProcessor.calculatePayment(pricingRules, accommodationPayments);
        //then
        // 10 * 1 + 20 * 1
        assertThat(reservationPaymentReport.getTotalCost(), equalTo(BigDecimal.valueOf(30)));
        List<AccommodationReport> accommodationReports = reservationPaymentReport.getAccommodationReports();
        assertThat(accommodationReports, hasSize(2));
    }

    @Test
    void calculatePaymentsShouldNotCalculateCostOfAccommodationWhenAAccommodationIsPaid() {
        //given
        accommodationPayments =  List.of(
                new AccommodationPayment("accommodationId", "reservationId", "B", 1, "departed", Rule.TENT,
                        true, LocalDate.of(2026, 6, 28), LocalDate.of(2026, 7, 29), true));
        pricingRules = getPricingRules();
        //when
        ReservationPaymentReport reservationPaymentReport = paymentProcessor.calculatePayment(pricingRules, accommodationPayments);
        //then
        // 10 * 1 + 20 * 1
        assertThat(reservationPaymentReport.getTotalCost(), equalTo(BigDecimal.valueOf(0)));
        List<AccommodationReport> accommodationReports = reservationPaymentReport.getAccommodationReports();
        assertThat(accommodationReports, hasSize(0));
    }

    @Test
    void calculatePaymentsShouldThrowExceptionWhenArrivalIsNotInSeasonPeriod() {
        //given
        accommodationPayments =  List.of(
                new AccommodationPayment("accommodationId", "reservationId", "B", 1, "departed", Rule.TENT,
                        true, LocalDate.of(2026, 12, 28), LocalDate.of(2026, 12, 29), false));
        pricingRules = getPricingRules();
        //when
        //then
        assertThrows(IllegalArgumentException.class,() -> paymentProcessor.calculatePayment(pricingRules, accommodationPayments));
    }

    @Test
    void calculatePaymentsShouldCalculatePaymentForMultipleAccommodations() {
        //given
        accommodationPayments =  List.of(
                new AccommodationPayment("accommodationId1", "reservationId", "B", 2, "departed", Rule.TENT,
                        false, LocalDate.of(2026, 6, 29), LocalDate.of(2026, 6, 30), false),
                new AccommodationPayment("accommodationId2", "reservationId", "B", 1, "departed", Rule.TENT,
                        false, LocalDate.of(2026, 6, 30), LocalDate.of(2026, 7, 1), false));
        pricingRules = getPricingRules();
        //when
        ReservationPaymentReport reservationPaymentReport = paymentProcessor.calculatePayment(pricingRules, accommodationPayments);
        //then
        // 10 * 2 * 1 + 20 * 1= 40
        assertThat(reservationPaymentReport.getTotalCost(), equalTo(BigDecimal.valueOf(40)));
        List<AccommodationReport> accommodationReports = reservationPaymentReport.getAccommodationReports();
        assertThat(accommodationReports, hasSize(2));
    }

    @Test
    void calculatePaymentShouldThrowExceptionWhenThereIsNoNightSpentOnCamping() {
        //given
        accommodationPayments =  List.of(
                new AccommodationPayment("accommodationId", "reservationId", "B", 1, "departed", Rule.TENT,
                        true, LocalDate.of(2026, 6, 28), LocalDate.of(2026, 6, 28), false));
        pricingRules = getPricingRules();
        //when
        //then
        assertThrows(IllegalArgumentException.class,() -> paymentProcessor.calculatePayment(pricingRules, accommodationPayments));
    }

}
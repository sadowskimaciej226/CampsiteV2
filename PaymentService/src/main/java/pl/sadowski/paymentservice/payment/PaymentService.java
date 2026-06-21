package pl.sadowski.paymentservice.payment;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sadowski.sdk.avro.AccommodationEvent;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
class PaymentService {

    private final AccommodationPaymentRepository paymentRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final PaymentProcessor paymentProcessor;

    @Transactional
    public void updateBill(AccommodationEvent accommodationEvent) {
        AccommodationPayment accommodationPayment = Mapper.mapToAccommodationPayment(accommodationEvent);
        paymentRepository.save(accommodationPayment);
    }

    public ReservationPaymentReport getPaymentForAccommodation(String reservationId,
                                                               @NonNull LocalDate arrivedAt,
                                                               @NonNull LocalDate departedAt) {
        List<PricingRule> pricingRules = pricingRuleRepository.findAll();
        List<AccommodationPayment> accommodationToBePaid = paymentRepository
                .findAccommodationPaymentForTime(reservationId, false, arrivedAt, departedAt);
        return paymentProcessor.calculatePayment(pricingRules, accommodationToBePaid);
    }
}

package pl.sadowski.paymentservice.payment;

import org.springframework.data.jpa.repository.JpaRepository;

interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
}

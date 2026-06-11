package pl.sadowski.paymentservice.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import pl.sadowski.sdk.avro.AccommodationEvent;

import static pl.sadowski.utils.Topics.RESERVATIONS_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
class KafkaAccommodationListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = RESERVATIONS_TOPIC,
            groupId = "${kafka.consumer.group-id}")
    void consumeAccommodation(AccommodationEvent event, Acknowledgment ack) {
        log.info("Received message: key={}, accommodationId={}", event.getReservationId(), event.getAccommodationId());
        ack.acknowledge();
    }

}

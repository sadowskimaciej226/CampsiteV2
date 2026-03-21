package pl.sadowski.bookingservice.reservation;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class KafkaConfiguration {

    @Bean
    public NewTopic reservation() {
        return new NewTopic("reservations", 1, (short) 1);
    }
}

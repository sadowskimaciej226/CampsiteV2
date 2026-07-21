package pl.sadowski.bookingservice.setup;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
class TestKafkaConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, Object> mockKafkaTemplate() {
        KafkaTemplate<String, Object> mock = mock(KafkaTemplate.class);
        when(mock.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(
                new SendResult<>(null, null)));
        return mock;
    }
}

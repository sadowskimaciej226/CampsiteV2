package pl.sadowski.bookingservice.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static pl.sadowski.utils.Topics.RESERVATIONS_TOPIC;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringJUnitConfig
@Import(TestKafkaConfig.class)
@EmbeddedKafka(
        partitions = 1,
        topics = {RESERVATIONS_TOPIC}
)
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private EmbeddedKafkaBroker broker;


}

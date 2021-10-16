package practice.pdfservice.config.rabbit;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:rabbitPdf.properties")
@Configuration
public class RabbitMqConfigStore {

    @Value("${queue.from.store.to.pdf}")
    private String queueFromStoreToPdf;


    @Bean
    Queue queueFromStoreToPdf() {
        return new Queue(queueFromStoreToPdf, true);
    }
}

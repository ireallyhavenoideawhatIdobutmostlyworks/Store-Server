package practice.pdfservice.config.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
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
    @Value("${exchange.from.store.to.pdf}")
    private String exchangeFromStoreToPdf;
    @Value("${routing.key.from.store.to.pdf}")
    private String routingKeyFromStoreToPdf;


    @Bean
    Queue queueFromStoreToPdf() {
        return new Queue(queueFromStoreToPdf, true);
    }

    @Bean
    DirectExchange exchangeFromStoreToPdf() {
        return new DirectExchange(exchangeFromStoreToPdf);
    }

    @Bean
    Binding bindingFromStoreToEPdf() {
        return BindingBuilder.bind(queueFromStoreToPdf()).to(exchangeFromStoreToPdf()).with(routingKeyFromStoreToPdf);
    }
}

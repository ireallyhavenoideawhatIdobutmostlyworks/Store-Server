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
public class RabbitMqConfigMail {

    @Value("${queue.from.pdf.to.email}")
    private String queueFromPdfToEmail;
    @Value("${exchange.from.pdf.to.email}")
    private String exchangeFromPdfToEmail;
    @Value("${routing.key.from.pdf.to.email}")
    private String routingKeyFromPdfToEmail;


    @Bean
    Queue queueFromPdfToEmail() {
        return new Queue(queueFromPdfToEmail, true);
    }

    @Bean
    DirectExchange exchangeFromPdfToEmail() {
        return new DirectExchange(exchangeFromPdfToEmail);
    }

    @Bean
    Binding bindingFromStoreToEmail() {
        return BindingBuilder.bind(queueFromPdfToEmail()).to(exchangeFromPdfToEmail()).with(routingKeyFromPdfToEmail);
    }
}

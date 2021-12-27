package practice.mailservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:rabbitMail.properties")
@TestConfiguration
public class RabbitMqConfigTest {

    @Value("${queue.from.bank.to.email}")
    private String queueFromBankToEmail;
    @Value("${exchange.from.bank.to.email}")
    private String exchangeFromBankToEmail;
    @Value("${routing.key.from.bank.to.email}")
    private String routingKeyFromBankToEmail;

    @Value("${queue.from.pdf.to.email}")
    private String queueFromPdfToEmail;
    @Value("${exchange.from.pdf.to.email}")
    private String exchangeFromPdfToEmail;
    @Value("${routing.key.from.pdf.to.email}")
    private String routingKeyFromPdfToEmail;

    @Value("${queue.from.store.to.email}")
    private String queueFromStoreToEmail;
    @Value("${exchange.from.store.to.email}")
    private String exchangeFromStoreToEmail;
    @Value("${routing.key.from.store.to.email}")
    private String routingKeyFromStoreToEmail;

    @Bean
    Queue queueFromBankToEmail() {
        return new Queue(queueFromBankToEmail, true);
    }

    @Bean
    DirectExchange exchangeFromBankToEmail() {
        return new DirectExchange(exchangeFromBankToEmail);
    }

    @Bean
    Binding bindingFromBankToEmail() {
        return BindingBuilder.bind(queueFromBankToEmail()).to(exchangeFromBankToEmail()).with(routingKeyFromBankToEmail);
    }

    @Bean
    Queue queueFromPdfToEmail() {
        return new Queue(queueFromPdfToEmail, true);
    }

    @Bean
    DirectExchange exchangeFromPdfToEmail() {
        return new DirectExchange(exchangeFromPdfToEmail);
    }

    @Bean
    Binding bindingFromPdfToEmail() {
        return BindingBuilder.bind(queueFromPdfToEmail()).to(exchangeFromPdfToEmail()).with(routingKeyFromPdfToEmail);
    }

    @Bean
    Queue queueFromStoreToEmail() {
        return new Queue(queueFromStoreToEmail, true);
    }

    @Bean
    DirectExchange exchangeFromStoreToEmail() {
        return new DirectExchange(exchangeFromStoreToEmail);
    }

    @Bean
    Binding bindingFromStoreToEmail() {
        return BindingBuilder.bind(queueFromStoreToEmail()).to(exchangeFromStoreToEmail()).with(routingKeyFromStoreToEmail);
    }
}
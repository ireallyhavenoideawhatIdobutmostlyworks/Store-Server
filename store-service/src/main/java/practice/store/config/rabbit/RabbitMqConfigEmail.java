package practice.store.config.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:rabbitStore.properties")
@Configuration
public class RabbitMqConfigEmail {

    @Value("${queue.from.store.to.email}")
    private String queueFromStoreToEmail;
    @Value("${exchange.from.store.to.email}")
    private String exchangeFromStoreToEmail;
    @Value("${routing.key.from.store.to.email}")
    private String routingKeyFromStoreToEmail;


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
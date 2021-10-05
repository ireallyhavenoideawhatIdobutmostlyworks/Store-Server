package practice.bank.config.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:rabbitBank.properties")
@Configuration
public class RabbitMqConfigEmail {

    @Value("${queue.from.bank.to.email}")
    private String queueFromBankToEmail;
    @Value("${exchange.from.bank.to.email}")
    private String exchangeFromBankToEmail;
    @Value("${routing.key.from.bank.to.email}")
    private String routingKeyFromBankToEmail;


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
}

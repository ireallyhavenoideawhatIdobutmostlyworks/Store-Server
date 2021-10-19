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
public class RabbitMqConfigStore {

    @Value("${queue.from.bank.to.store}")
    private String queueFromBankToStore;
    @Value("${exchange.from.bank.to.store}")
    private String exchangeFromBankToStore;
    @Value("${routing.key.from.bank.to.store}")
    private String routingKeyFromBankToStore;


    @Bean
    Queue queueFromBankToStore() {
        return new Queue(queueFromBankToStore, true);
    }

    @Bean
    DirectExchange exchangeFromBankToStore() {
        return new DirectExchange(exchangeFromBankToStore);
    }

    @Bean
    Binding bindingFromBankToStore() {
        return BindingBuilder.bind(queueFromBankToStore()).to(exchangeFromBankToStore()).with(routingKeyFromBankToStore);
    }
}

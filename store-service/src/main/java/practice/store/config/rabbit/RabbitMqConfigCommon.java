package practice.store.config.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:rabbit.properties")
@Configuration
public class RabbitMqConfig {

    @Value("${queue.common}")
    private String queueCommon;
    @Value("${queue.from.bank}")
    private String bankToStoreQueue;
    @Value("${exchange.common}")
    private String exchangeCommon;
    @Value("${routing.key.common}")
    private String routingKeyNameCommon;
    @Value("${connection.name}")
    private String connectionName;


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory jsaFactory(ConnectionFactory connectionFactory, SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    Queue queueCommon() {
        return new Queue(queueCommon, true);
    }

    @Bean
    Queue queueFromBank() {
        return new Queue(bankToStoreQueue, true);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchangeCommon);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKeyNameCommon);
    }

    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SmartInitializingSingleton reconfigureCf(final CachingConnectionFactory cf) {
        return () -> cf.setConnectionNameStrategy(f -> connectionName);
    }
}


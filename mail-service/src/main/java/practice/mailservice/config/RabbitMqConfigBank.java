//package practice.mailservice.config;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.DirectExchange;
//import org.springframework.amqp.core.Queue;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//
//@PropertySource("classpath:rabbitMail.properties")
//@Configuration
//public class RabbitMqConfigBank {
//
//    @Value("${queue.from.bank.to.email}")
//    private String queueToMailFromBank;
//
//
//    @Bean
//    Queue queueToMailFromBank() {
//        return new Queue(queueToMailFromBank, true);
//    }
//
//}

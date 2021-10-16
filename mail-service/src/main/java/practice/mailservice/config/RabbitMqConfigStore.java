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
//public class RabbitMqConfigStore {
//
//    @Value("${queue.from.store.to.email}")
//    private String queueToMailFromStore;
////    @Value("${exchange.to.mail.from.store}")
////    private String exchangeToMailFromStore;
////    @Value("${routing.key.to.mail.from.store}")
////    private String routingKeyToMailFromStore;
//
//
//    @Bean
//    Queue queueToMailFromStore() {
//        return new Queue(queueToMailFromStore, true);
//    }
//
////    @Bean
////    DirectExchange exchangeToMailFromStore() {
////        return new DirectExchange(exchangeToMailFromStore);
////    }
////
////    @Bean
////    Binding bindingToMailFromStore() {
////        return BindingBuilder.bind(queueToMailFromStore()).to(exchangeToMailFromStore()).with(routingKeyToMailFromStore);
////    }
//}

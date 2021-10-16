//package practice.mailservice.config;
//
//import org.springframework.amqp.core.Queue;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//
//@PropertySource("classpath:rabbitMail.properties")
//@Configuration
//public class RabbitMqConfigPdf {
//
//    @Value("${queue.from.pdf.to.email}")
//    private String queueFromPdfToEmail;
//
//
//    @Bean
//    Queue queueToMailFromPdf() {
//        return new Queue(queueFromPdfToEmail, true);
//    }
//}

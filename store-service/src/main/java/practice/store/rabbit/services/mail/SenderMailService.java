package practice.store.rabbit.services.mail;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.store.order.OrderEntity;

@PropertySource("classpath:rabbitStore.properties")
@Service
@Log4j2
public class SenderMailService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.from.store.to.email}")
    private String queueFromStoreToEmail;


    public void send(OrderEntity order) {
        SenderMailPayload senderMailPayload = preparePublisherPayload(order);
        rabbitTemplate.convertAndSend(queueFromStoreToEmail, senderMailPayload);
        log.info("Send mailPayload object to mail-service. Payload: {}", senderMailPayload);
    }


    private SenderMailPayload preparePublisherPayload(OrderEntity order) {
        return SenderMailPayload.builder()
                .orderUUID(order.getOrderUUID())
                .paymentUUID(order.getPaymentUUID())
                .accountNumber(order.getAccountNumber())
                .email(order.getCustomer().getEmail())
                .build();
    }
}

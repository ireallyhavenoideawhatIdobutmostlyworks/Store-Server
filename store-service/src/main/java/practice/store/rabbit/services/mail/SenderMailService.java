package practice.store.rabbit.services.mail;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.store.order.OrderEntity;

@PropertySource("classpath:rabbit.properties")
@Service
public class SenderMailService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.to.email}")
    private String queue;


    public void send(OrderEntity order) {
        SenderMailPayload payload = preparePublisherPayload(order);
        rabbitTemplate.convertAndSend(queue, payload);
    }


    private SenderMailPayload preparePublisherPayload(OrderEntity order) {
        return SenderMailPayload.builder()
                .orderUUID(order.getOrderUUID())
                .paymentUUID(order.getPaymentUUID())
                .orderPrice(order.getOrderFinalPrice())
                .accountNumber(order.getAccountNumber())
                .email(order.getCustomer().getEmail())
                .build();
    }
}

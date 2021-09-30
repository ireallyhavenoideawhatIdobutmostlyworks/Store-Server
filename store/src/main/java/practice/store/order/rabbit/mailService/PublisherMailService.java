package practice.store.order.rabbit.mailService;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.store.order.OrderEntity;

@PropertySource("classpath:rabbit.properties")
@Service
public class PublisherMailService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.to.email}")
    private String queue;


    public void send(OrderEntity order) {
        PublisherMailPayload payload = preparePublisherPayload(order);
        rabbitTemplate.convertAndSend(queue, payload);
    }


    private PublisherMailPayload preparePublisherPayload(OrderEntity order) {
        return PublisherMailPayload.builder()
                .orderUUID(order.getOrderUUID())
                .paymentUUID(order.getPaymentUUID())
                .orderPrice(order.getOrderFinalPrice())
                .accountNumber(order.getAccountNumber())
                .email(order.getCustomer().getEmail())
                .build();
    }
}

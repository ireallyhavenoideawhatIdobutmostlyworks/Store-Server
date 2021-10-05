package practice.bank.rabbit.store;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.bank.payment.PaymentEntity;

@PropertySource("classpath:rabbitBank.properties")
@Service
public class SenderStoreService {

    @Autowired
    private RabbitTemplate template;

    @Value("${queue.from.bank.to.store}")
    private String queueFromBankToStore;


    public void send(PaymentEntity paymentEntity) {
        SenderStorePayload senderStorePayload = prepareSenderPayload(paymentEntity);
        template.convertAndSend(queueFromBankToStore, senderStorePayload);
    }


    private SenderStorePayload prepareSenderPayload(PaymentEntity paymentEntity) {
        return SenderStorePayload.builder()
                .orderUUID(paymentEntity.getOrderUUID())
                .accountNumber(paymentEntity.getAccountNumber())
                .paymentUUID(paymentEntity.getPaymentUUID())
                .orderPrice(paymentEntity.getOrderPrice())
                .isPaymentSuccess(paymentEntity.getIsPaymentSuccess())
                .paymentType(paymentEntity.getPaymentType())
                .build();
    }
}

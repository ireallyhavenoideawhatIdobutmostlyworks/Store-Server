package practice.bank.rabbit.mail;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import practice.bank.payment.PaymentEntity;
import practice.bank.payment.PaymentResultPayload;

@PropertySource("classpath:rabbitBank.properties")
@Service
public class SenderMailService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${queue.from.bank.to.email}")
    private String queueFromBankToEmail;


    public void send(PaymentEntity paymentEntity) {
        SenderMailPayload senderMailPayload = prepareSenderPayload(paymentEntity);
        rabbitTemplate.convertAndSend(queueFromBankToEmail, senderMailPayload);
    }

    public void send(PaymentResultPayload paymentResultPayload) {
        SenderMailPayload senderMailPayload = prepareSenderPayload(paymentResultPayload);
        rabbitTemplate.convertAndSend(queueFromBankToEmail, senderMailPayload);
    }


    private SenderMailPayload prepareSenderPayload(PaymentEntity paymentEntity) {
        return SenderMailPayload.builder()
                .orderUUID(paymentEntity.getOrderUUID())
                .accountNumber(paymentEntity.getAccountNumber())
                .paymentUUID(paymentEntity.getPaymentUUID())
                .orderPrice(paymentEntity.getOrderPrice())
                .paymentType(paymentEntity.getPaymentType())
                .isPaymentSuccess(paymentEntity.getIsPaymentSuccess())
                .build();
    }

    private SenderMailPayload prepareSenderPayload(PaymentResultPayload paymentResultPayload) {
        return SenderMailPayload.builder()
                .orderUUID(paymentResultPayload.getOrderUUID())
                .accountNumber(paymentResultPayload.getAccountNumber())
                .paymentUUID(paymentResultPayload.getPaymentUUID())
                .orderPrice(paymentResultPayload.getOrderPrice())
                .isPaymentSuccess(paymentResultPayload.getIsPaymentSuccess())
                .paymentType(paymentResultPayload.getPaymentType())
                .build();
    }
}

package practice.bank.payment;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.IBANValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.bank.rabbit.mail.SenderMailPayload;
import practice.bank.rabbit.mail.SenderMailService;
import practice.bank.rabbit.store.SenderStorePayload;
import practice.bank.rabbit.store.SenderStoreService;
import practice.bank.utils.GenerateRandomString;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@PropertySource("classpath:payment.properties")
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SenderStoreService senderStoreService;
    private final SenderMailService senderMailService;
    private final GenerateRandomString generateRandomString;

    @Value("${timeout.simulation}")
    private long timeoutSimulation;


    @Transactional
    public boolean processingPayment(PaymentResultPayload paymentResultPayload) throws InterruptedException {
        String paymentUUID = generateRandomString.generateRandomUuid();

        delayPaymentProcessSimulation();

        boolean isPaymentSuccess = isPaymentSuccess(paymentResultPayload) && ibanValidator(paymentResultPayload);

        PaymentEntity paymentEntity = preparePaymentEntity(paymentResultPayload, paymentUUID, isPaymentSuccess);
        paymentRepository.save(paymentEntity);

        senderStoreService.send(prepareSenderStorePayload(paymentEntity));
        senderMailService.send(prepareSenderMailPayload(paymentEntity));

        return isPaymentSuccess;
    }

    private boolean isPaymentSuccess(PaymentResultPayload paymentResultPayload) {
        return paymentResultPayload.getIsPaymentSuccess();
    }

    private boolean ibanValidator(PaymentResultPayload paymentResultPayload) {
        return new IBANValidator().isValid(paymentResultPayload.getAccountNumber());
    }

    private void delayPaymentProcessSimulation() throws InterruptedException {
        TimeUnit.SECONDS.sleep(timeoutSimulation);
    }

    private PaymentEntity preparePaymentEntity(PaymentResultPayload paymentResultPayload, String paymentUUID, boolean isPaymentSuccess) {
        return PaymentEntity.builder()
                .orderUUID(paymentResultPayload.getOrderUUID())
                .accountNumber(paymentResultPayload.getAccountNumber())
                .paymentUUID(paymentUUID)
                .email(paymentResultPayload.getEmail())
                .orderPrice(paymentResultPayload.getOrderPrice())
                .isPaymentSuccess(isPaymentSuccess)
                .processingDate(LocalDateTime.now())
                .paymentType(paymentResultPayload.getPaymentType())
                .build();
    }

    private SenderMailPayload prepareSenderMailPayload(PaymentEntity paymentEntity) {
        return SenderMailPayload.builder()
                .orderUUID(paymentEntity.getOrderUUID())
                .accountNumber(paymentEntity.getAccountNumber())
                .paymentUUID(paymentEntity.getPaymentUUID())
                .email(paymentEntity.getEmail())
                .orderPrice(paymentEntity.getOrderPrice())
                .paymentType(paymentEntity.getPaymentType())
                .isPaymentSuccess(paymentEntity.getIsPaymentSuccess())
                .build();
    }

    private SenderStorePayload prepareSenderStorePayload(PaymentEntity paymentEntity) {
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

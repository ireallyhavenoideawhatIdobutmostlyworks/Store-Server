package practice.bank.payment;

import lombok.RequiredArgsConstructor;
import org.iban4j.IbanFormatException;
import org.iban4j.IbanUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.bank.exceptions.PaymentFailureException;
import practice.bank.rabbit.mail.SenderMailService;
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


    @Transactional(noRollbackFor = RuntimeException.class)
    public void processingPayment(PaymentResultPayload paymentResultPayload) throws InterruptedException {
        String paymentUUID = generateRandomString.generateRandomUuid();

        delayPaymentProcessSimulation();
        checkIfPaymentSuccess(paymentResultPayload, paymentUUID);
        checkIfIbanFormatIsValid(paymentResultPayload, paymentUUID);

        PaymentEntity paymentEntity = preparePaymentEntity(paymentResultPayload, paymentUUID, true);
        paymentRepository.save(paymentEntity);

        senderStoreService.send(paymentEntity);
        senderMailService.send(paymentEntity);
    }


    private void delayPaymentProcessSimulation() throws InterruptedException {
        TimeUnit.SECONDS.sleep(timeoutSimulation);
    }

    private void checkIfPaymentSuccess(PaymentResultPayload paymentResultPayload, String paymentUUID) {
        if (!paymentResultPayload.getIsPaymentSuccess()) {
            paymentRepository.save(preparePaymentEntity(paymentResultPayload, paymentUUID, false));
            senderMailService.send(paymentResultPayload);
            throw new PaymentFailureException(paymentResultPayload, paymentUUID);
        }
    }

    private void checkIfIbanFormatIsValid(PaymentResultPayload paymentResultPayload, String paymentUUID) {
        try {
            IbanUtil.validate(paymentResultPayload.getAccountNumber());
        } catch (IbanFormatException e) {
            paymentRepository.save(preparePaymentEntity(paymentResultPayload, paymentUUID, false));
            senderMailService.send(paymentResultPayload);
            throw new PaymentFailureException(paymentResultPayload, paymentUUID);
        }
    }

    private PaymentEntity preparePaymentEntity(PaymentResultPayload paymentResultPayload, String paymentUUID, boolean isPaymentSuccess) {
        return PaymentEntity.builder()
                .orderUUID(paymentResultPayload.getOrderUUID())
                .accountNumber(paymentResultPayload.getAccountNumber())
                .paymentUUID(paymentUUID)
                .orderPrice(paymentResultPayload.getOrderPrice())
                .isPaymentSuccess(isPaymentSuccess)
                .processingDate(LocalDateTime.now())
                .paymentType(paymentResultPayload.getPaymentType())
                .build();
    }
}

package practice.bank.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.validator.routines.IBANValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.bank.rabbit.mail.SenderMailPayload;
import practice.bank.rabbit.mail.SenderMailService;
import practice.bank.rabbit.store.SenderStorePayload;
import practice.bank.rabbit.store.SenderStoreService;
import practice.bank.utils.GenerateRandomString;

import java.math.RoundingMode;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Log4j2
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SenderStoreService senderStoreService;
    private final SenderMailService senderMailService;
    private final GenerateRandomString generateRandomString;


    @Transactional
    public boolean processingPayment(PaymentResultPayload paymentResultPayload) {
        String paymentUUID = generateRandomString.generateRandomUuid();
        log.info("Payment UUID: {}", paymentUUID);

        boolean isPaymentSuccess = isPaymentSuccess(paymentResultPayload) && ibanValidator(paymentResultPayload);

        PaymentEntity paymentEntity = preparePaymentEntity(paymentResultPayload, paymentUUID, isPaymentSuccess);
        paymentRepository.save(paymentEntity);

        senderStoreService.send(prepareSenderStorePayload(paymentEntity));
        senderMailService.send(prepareSenderMailPayload(paymentEntity));

        return isPaymentSuccess;
    }


    private boolean isPaymentSuccess(PaymentResultPayload paymentResultPayload) {
        boolean isPaymentSuccess = paymentResultPayload.getIsPaymentSuccess();
        log.info("Is payment success: {}", isPaymentSuccess);
        return isPaymentSuccess;
    }

    private boolean ibanValidator(PaymentResultPayload paymentResultPayload) {
        boolean isAccountIbanValid = new IBANValidator().isValid(paymentResultPayload.getAccountNumber());
        log.info("Is account IBAN valid: {}", isAccountIbanValid);
        return isAccountIbanValid;
    }

    private PaymentEntity preparePaymentEntity(PaymentResultPayload paymentResultPayload, String paymentUUID, boolean isPaymentSuccess) {
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .orderUUID(paymentResultPayload.getOrderUUID())
                .accountNumber(paymentResultPayload.getAccountNumber())
                .paymentUUID(paymentUUID)
                .email(paymentResultPayload.getEmail())
                .orderPrice(paymentResultPayload.getOrderPrice().setScale(2, RoundingMode.CEILING))
                .isPaymentSuccess(isPaymentSuccess)
                .processingDate(LocalDateTime.now())
                .paymentType(paymentResultPayload.getPaymentType())
                .build();

        log.info("Prepare payment entity: {}", paymentEntity);
        return paymentEntity;
    }

    private SenderMailPayload prepareSenderMailPayload(PaymentEntity paymentEntity) {
        SenderMailPayload senderMailPayload = SenderMailPayload.builder()
                .orderUUID(paymentEntity.getOrderUUID())
                .accountNumber(paymentEntity.getAccountNumber())
                .paymentUUID(paymentEntity.getPaymentUUID())
                .email(paymentEntity.getEmail())
                .orderPrice(paymentEntity.getOrderPrice())
                .paymentType(paymentEntity.getPaymentType())
                .isPaymentSuccess(paymentEntity.getIsPaymentSuccess())
                .build();

        log.info("Prepare sender mail payload: {}", senderMailPayload);
        return senderMailPayload;
    }

    private SenderStorePayload prepareSenderStorePayload(PaymentEntity paymentEntity) {
        SenderStorePayload senderStorePayload = SenderStorePayload.builder()
                .orderUUID(paymentEntity.getOrderUUID())
                .accountNumber(paymentEntity.getAccountNumber())
                .paymentUUID(paymentEntity.getPaymentUUID())
                .orderPrice(paymentEntity.getOrderPrice())
                .isPaymentSuccess(paymentEntity.getIsPaymentSuccess())
                .paymentType(paymentEntity.getPaymentType())
                .build();

        log.info("Prepare sender store payload: {}", senderStorePayload);
        return senderStorePayload;
    }
}

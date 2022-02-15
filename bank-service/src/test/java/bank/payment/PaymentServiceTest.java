package bank.payment;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import practice.bank.payment.PaymentEntity;
import practice.bank.payment.PaymentRepository;
import practice.bank.payment.PaymentResultPayload;
import practice.bank.payment.PaymentService;
import practice.bank.rabbit.mail.SenderMailService;
import practice.bank.rabbit.store.SenderStoreService;
import practice.bank.utils.GenerateRandomString;
import testdata.TestData;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests for payment service")
@ExtendWith(MockitoExtension.class)
@Tag("Unit_Test")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private final static String simpleUuid = new GenerateRandomString().generateRandomUuid();

    private final String queueNameStore = "queue.from.bank.to.store";
    private final String queueNameEmail = "queue.from.bank.to.email";
    private final String validIban = "AT611904300234573201";

    private PaymentResultPayload paymentResultPayload;


    @BeforeAll
    static void mockUUid() {
        UUID uuidJavaUtil = UUID.fromString(simpleUuid);
        mockStatic(UUID.class);
        when(UUID.randomUUID()).thenReturn(uuidJavaUtil);
    }

    @BeforeEach
    void setUp() {
        SenderStoreService senderStoreService = new SenderStoreService(rabbitTemplate);
        SenderMailService senderMailService = new SenderMailService(rabbitTemplate);
        paymentService = new PaymentService(paymentRepository, senderStoreService, senderMailService, new GenerateRandomString());

        ReflectionTestUtils.setField(senderStoreService, "queueFromBankToStore", queueNameStore);
        ReflectionTestUtils.setField(senderMailService, "queueFromBankToEmail", queueNameEmail);
    }

    @DisplayName("Processing payment with success")
    @Test
    void processingPayment_Succeed() {
        // given
        paymentResultPayload = TestData.paymentResultPayload(validIban, true);
        PaymentEntity paymentEntity = TestData.preparePaymentEntity(paymentResultPayload, simpleUuid, true);


        // when
        boolean processPayment = paymentService.processingPayment(paymentResultPayload);


        // then
        ArgumentCaptor<PaymentEntity> argument = ArgumentCaptor.forClass(PaymentEntity.class);
        assertAll(
                () -> verify(paymentRepository).save(argument.capture()),
                () -> verify(rabbitTemplate, times(1)).convertAndSend(queueNameStore, TestData.prepareSenderStorePayload(paymentEntity)),
                () -> verify(rabbitTemplate, times(1)).convertAndSend(queueNameEmail, TestData.prepareSenderMailPayload(paymentEntity)),

                () -> assertThat(argument.getValue())
                        .usingRecursiveComparison()
                        .ignoringFields("paymentUUID", "processingDate")
                        .isEqualTo(paymentEntity),
                () -> assertTrue(processPayment)
        );
    }

    @DisplayName("Fail payment if account format is not IBAN valid")
    @Test
    void processingPayment_Failed_IfIbanIsInvalid() {
        // given
        paymentResultPayload = TestData.paymentResultPayload("invalid iban", true);
        PaymentEntity paymentEntity = TestData.preparePaymentEntity(paymentResultPayload, simpleUuid, false);


        // when
        boolean processPayment = paymentService.processingPayment(paymentResultPayload);


        // then
        ArgumentCaptor<PaymentEntity> argument = ArgumentCaptor.forClass(PaymentEntity.class);
        assertAll(
                () -> verify(paymentRepository).save(argument.capture()),
                () -> verify(rabbitTemplate, times(1)).convertAndSend(queueNameStore, TestData.prepareSenderStorePayload(paymentEntity)),
                () -> verify(rabbitTemplate, times(1)).convertAndSend(queueNameEmail, TestData.prepareSenderMailPayload(paymentEntity)),

                () -> assertThat(argument.getValue())
                        .usingRecursiveComparison()
                        .ignoringFields("paymentUUID", "processingDate", "isPaymentSuccess")
                        .isEqualTo(paymentEntity),
                () -> assertFalse(argument.getValue().getIsPaymentSuccess()),
                () -> assertFalse(processPayment)
        );
    }

    @DisplayName("Fail payment if isPaymentSuccess is false")
    @Test
    void processingPayment_Failed_IfIsSuccessFieldIsFalse() {
        // given
        paymentResultPayload = TestData.paymentResultPayload(validIban, false);
        PaymentEntity paymentEntity = TestData.preparePaymentEntity(paymentResultPayload, simpleUuid, false);


        // when
        boolean processPayment = paymentService.processingPayment(paymentResultPayload);


        // then
        ArgumentCaptor<PaymentEntity> argument = ArgumentCaptor.forClass(PaymentEntity.class);
        assertAll(
                () -> verify(paymentRepository).save(argument.capture()),
                () -> verify(rabbitTemplate, times(1)).convertAndSend(queueNameStore, TestData.prepareSenderStorePayload(paymentEntity)),
                () -> verify(rabbitTemplate, times(1)).convertAndSend(queueNameEmail, TestData.prepareSenderMailPayload(paymentEntity)),

                () -> assertThat(argument.getValue())
                        .usingRecursiveComparison()
                        .ignoringFields("paymentUUID", "processingDate")
                        .isEqualTo(paymentEntity),
                () -> assertFalse(argument.getValue().getIsPaymentSuccess()),
                () -> assertFalse(processPayment)
        );
    }
}

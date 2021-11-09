package bank.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import practice.bank.payment.PaymentEntity;
import practice.bank.payment.PaymentRepository;
import practice.bank.payment.PaymentResultPayload;
import practice.bank.payment.PaymentService;
import practice.bank.rabbit.mail.SenderMailService;
import practice.bank.rabbit.store.SenderStoreService;
import practice.bank.utils.GenerateRandomString;
import testdata.TestData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DisplayName("Tests for payment service")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @BeforeEach
    void setUp() {
        SenderStoreService senderStoreService = new SenderStoreService(rabbitTemplate);
        SenderMailService senderMailService = new SenderMailService(rabbitTemplate);
        paymentService = new PaymentService(paymentRepository, senderStoreService, senderMailService, new GenerateRandomString());
    }


    @DisplayName("Processing payment with success")
    @Test
    void should_add_payment_with_correct_data() throws InterruptedException {
        // given
        PaymentResultPayload paymentResultPayload = TestData.paymentResultPayload("AT611904300234573201", true);
        PaymentEntity paymentEntity = TestData.paymentEntity(paymentResultPayload);


        // when
        boolean processPayment = paymentService.processingPayment(paymentResultPayload);


        // then
        ArgumentCaptor<PaymentEntity> argument = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(argument.capture());
        assertEquals(paymentEntity.getOrderUUID(), argument.getValue().getOrderUUID());
        assertEquals(paymentEntity.getAccountNumber(), argument.getValue().getAccountNumber());
        assertEquals(paymentEntity.getEmail(), argument.getValue().getEmail());
        assertEquals(paymentEntity.getOrderPrice(), argument.getValue().getOrderPrice());
        assertEquals(paymentEntity.getIsPaymentSuccess(), argument.getValue().getIsPaymentSuccess());
        assertEquals(paymentEntity.getPaymentType(), argument.getValue().getPaymentType());

        assertTrue(processPayment);
    }

    @DisplayName("Fail payment if account format is not IBAN valid")
    @Test
    void should_payment_be_false_when_account_number_is_incorrect() throws InterruptedException {
        // given
        PaymentResultPayload paymentResultPayload = TestData.paymentResultPayload("not IBAN valid account number", true);
        PaymentEntity paymentEntity = TestData.paymentEntity(paymentResultPayload);


        // when
        boolean processPayment = paymentService.processingPayment(paymentResultPayload);


        // then
        ArgumentCaptor<PaymentEntity> argument = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(argument.capture());
        assertEquals(paymentEntity.getOrderUUID(), argument.getValue().getOrderUUID());
        assertEquals(paymentEntity.getAccountNumber(), argument.getValue().getAccountNumber());
        assertEquals(paymentEntity.getEmail(), argument.getValue().getEmail());
        assertEquals(paymentEntity.getOrderPrice(), argument.getValue().getOrderPrice());
        assertEquals(paymentEntity.getIsPaymentSuccess(), !argument.getValue().getIsPaymentSuccess());
        assertEquals(paymentEntity.getPaymentType(), argument.getValue().getPaymentType());

        assertFalse(processPayment);
    }

    @DisplayName("Fail payment if isPaymentSuccess is false")
    @Test
    void should_payment_be_false_when_isPaymentSuccess_field_is_false() throws InterruptedException {
        // given
        PaymentResultPayload paymentResultPayload = TestData.paymentResultPayload("AT611904300234573201", false);
        PaymentEntity paymentEntity = TestData.paymentEntity(paymentResultPayload);


        // when
        boolean processPayment = paymentService.processingPayment(paymentResultPayload);


        // then
        ArgumentCaptor<PaymentEntity> argument = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(argument.capture());
        assertEquals(paymentEntity.getOrderUUID(), argument.getValue().getOrderUUID());
        assertEquals(paymentEntity.getAccountNumber(), argument.getValue().getAccountNumber());
        assertEquals(paymentEntity.getEmail(), argument.getValue().getEmail());
        assertEquals(paymentEntity.getOrderPrice(), argument.getValue().getOrderPrice());
        assertEquals(paymentEntity.getIsPaymentSuccess(), argument.getValue().getIsPaymentSuccess());
        assertEquals(paymentEntity.getPaymentType(), argument.getValue().getPaymentType());

        assertFalse(processPayment);
    }
}

package bank.payment;

import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.junit.RabbitAvailable;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import practice.bank.config.rabbit.RabbitMqConfigCommon;
import practice.bank.payment.*;
import practice.bank.rabbit.mail.SenderMailPayload;
import practice.bank.rabbit.mail.SenderMailService;
import practice.bank.rabbit.store.SenderStorePayload;
import practice.bank.rabbit.store.SenderStoreService;
import practice.bank.utils.GenerateRandomString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

//@RabbitAvailable
@TestPropertySource("classpath:test.properties")
//@Testcontainers
@DisplayName("Tests for customer service")
@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class PaymentServiceTest {

//    @ClassRule
//    public static GenericContainer<?> rabbitMQContainer = new GenericContainer<>("rabbitmq:management")
//            .withExposedPorts(5672);

    private RabbitMqConfigCommon rabbitMqConfigCommon;
  

    @InjectMocks
    private SenderStoreService senderStoreService;
    private SenderMailService senderMailService;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    private GenerateRandomString generateRandomString;


    @BeforeEach
    void setUp() {
        senderStoreService = new SenderStoreService();
        senderMailService = new SenderMailService();
        generateRandomString = new GenerateRandomString();
        paymentService = new PaymentService(paymentRepository, senderStoreService, senderMailService, generateRandomString);
    }


    @DisplayName("Processing payment with success")
    @Test
    void testtest() throws InterruptedException {
        // given
        boolean isPaymentSuccess = true;
        PaymentResultPayload paymentResultPayload = paymentResultPayload("AT611904300234573201", isPaymentSuccess);
        PaymentEntity paymentEntity = paymentEntity(paymentResultPayload, 1L, isPaymentSuccess);
        doNothing().when(rabbitTemplate).convertAndSend("queue.from.bank.to.store", paymentEntity);



        // when
        boolean processingPayment = paymentService.processingPayment(paymentResultPayload);


        // then

    }


    private PaymentResultPayload paymentResultPayload(String accountNumber, boolean isPaymentSuccess) {
        return PaymentResultPayload.builder()
                .orderUUID(generateRandomString.generateRandomUuid())
                .orderPrice(BigDecimal.valueOf(100))
                .accountNumber(accountNumber)
                .paymentType(PaymentType.BLIK)
                .email("some@test.email")
                .isPaymentSuccess(isPaymentSuccess)
                .build();
    }

    private SenderStorePayload senderStorePayload(PaymentEntity paymentEntity) {
        return SenderStorePayload.builder()
                .orderUUID(paymentEntity.getOrderUUID())
                .orderPrice(paymentEntity.getOrderPrice())
                .accountNumber(paymentEntity.getAccountNumber())
                .paymentType(paymentEntity.getPaymentType())
                .isPaymentSuccess(paymentEntity.getIsPaymentSuccess())
                .build();
    }

    private SenderMailPayload senderMailPayload(PaymentEntity paymentEntity) {
        return SenderMailPayload.builder()
                .orderUUID(paymentEntity.getOrderUUID())
                .paymentUUID(paymentEntity.getPaymentUUID())
                .orderPrice(paymentEntity.getOrderPrice())
                .accountNumber(paymentEntity.getAccountNumber())
                .email(paymentEntity.getEmail())
                .paymentType(paymentEntity.getPaymentType())
                .isPaymentSuccess(paymentEntity.getIsPaymentSuccess())
                .build();
    }

    private PaymentEntity paymentEntity(PaymentResultPayload paymentResultPayload, long id, boolean isPaymentSuccess) {
        return PaymentEntity.builder()
                .id(id)
                .orderUUID(paymentResultPayload.getOrderUUID())
                .paymentUUID(generateRandomString.generateRandomUuid())
                .accountNumber(paymentResultPayload.getAccountNumber())
                .email(paymentResultPayload.getEmail())
                .orderPrice(paymentResultPayload.getOrderPrice())
                .isPaymentSuccess(isPaymentSuccess)
                .processingDate(LocalDateTime.now())
                .paymentType(paymentResultPayload.getPaymentType())
                .build();
    }

}

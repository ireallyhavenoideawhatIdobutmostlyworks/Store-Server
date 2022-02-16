package bank.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import practice.bank.BankApplication;
import practice.bank.payment.PaymentEntity;
import practice.bank.payment.PaymentRepository;
import practice.bank.payment.PaymentResultPayload;
import practice.bank.rabbit.mail.SenderMailPayload;
import practice.bank.rabbit.store.SenderStorePayload;
import testdata.TestData;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@Tag("Bank_Service_Integration_Test")
@Transactional
class PaymentControllerTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final String validIban = "AT611904300234573201";
    private PaymentResultPayload paymentResultPayload;


    @BeforeAll
    public static void setRabbitMqContainer() {
        runRabbitMqContainer();
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void processingPayment_Succeed() throws Exception {
        // given
        paymentResultPayload = TestData.paymentResultPayload(validIban, true);


        // when
        performPostRequest(201, paymentResultPayload);


        // then
        PaymentEntity paymentEntity = paymentRepository.findByOrderUUID(paymentResultPayload.getOrderUUID());
        assertAll(
                () -> assertNotNull(paymentEntity.getPaymentUUID()),
                () -> assertTrue(paymentEntity.getIsPaymentSuccess()),

                () -> assertThat(paymentEntity)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "email", "processingDate")
                        .isEqualTo(queueStoreBody()),
                () -> assertThat(paymentEntity)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "processingDate")
                        .isEqualTo(queueMailBody()),
                () -> assertThat(paymentEntity)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "paymentUUID", "processingDate", "isPaymentSuccess")
                        .isEqualTo(paymentResultPayload)
        );
    }

    @Test
    void processingPayment_Failed_IfIsSuccessFieldIsFalse() throws Exception {
        // given
        paymentResultPayload = TestData.paymentResultPayload(validIban, false);


        // when
        performPostRequest(418, paymentResultPayload);


        // then
        PaymentEntity paymentEntity = paymentRepository.findByOrderUUID(paymentResultPayload.getOrderUUID());
        assertAll(
                () -> assertNotNull(paymentEntity.getPaymentUUID()),
                () -> assertFalse(paymentEntity.getIsPaymentSuccess()),

                () -> assertThat(paymentEntity)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "email", "processingDate")
                        .isEqualTo(queueStoreBody()),
                () -> assertThat(paymentEntity)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "processingDate")
                        .isEqualTo(queueMailBody()),
                () -> assertThat(paymentEntity)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "paymentUUID", "processingDate", "isPaymentSuccess")
                        .isEqualTo(paymentResultPayload)
        );
    }

    @Test
    void processingPayment_Failed_IfIbanIsInvalid() throws Exception {
        // given
        paymentResultPayload = TestData.paymentResultPayload("invalid iban", true);


        // when
        performPostRequest(418, paymentResultPayload);


        // then
        PaymentEntity paymentEntity = paymentRepository.findByOrderUUID(paymentResultPayload.getOrderUUID());
        assertAll(
                () -> assertNotNull(paymentEntity.getPaymentUUID()),
                () -> assertFalse(paymentEntity.getIsPaymentSuccess()),

                () -> assertThat(paymentEntity)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "email", "processingDate")
                        .isEqualTo(queueStoreBody()),
                () -> assertThat(paymentEntity)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "processingDate")
                        .isEqualTo(queueMailBody()),
                () -> assertThat(paymentEntity)
                        .usingRecursiveComparison()
                        .ignoringFields("id", "paymentUUID", "processingDate", "isPaymentSuccess")
                        .isEqualTo(paymentResultPayload)
        );
    }


    private void performPostRequest(int expectStatusCode, PaymentResultPayload payload) throws Exception {
        mvc
                .perform(MockMvcRequestBuilders
                        .post("/api/payment/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(payload)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(expectStatusCode));
    }

    private SenderStorePayload queueStoreBody() throws JsonProcessingException {
        String queueBody = new String(Objects.requireNonNull(rabbitTemplate.receive("queue.from.bank.to.store")).getBody(), StandardCharsets.UTF_8);
        return new ObjectMapper().readValue(queueBody, SenderStorePayload.class);
    }

    private SenderMailPayload queueMailBody() throws JsonProcessingException {
        String queueBody = new String(Objects.requireNonNull(rabbitTemplate.receive("queue.from.bank.to.email")).getBody(), StandardCharsets.UTF_8);
        return new ObjectMapper().readValue(queueBody, SenderMailPayload.class);
    }

    private static void runRabbitMqContainer() {
        final GenericContainer rabbitMq = new GenericContainer("rabbitmq:3-management").withExposedPorts(5672);
        rabbitMq.start();

        System.setProperty("spring.rabbitmq.host", rabbitMq.getContainerIpAddress());
        System.setProperty("spring.rabbitmq.port", rabbitMq.getMappedPort(5672).toString());
    }
}


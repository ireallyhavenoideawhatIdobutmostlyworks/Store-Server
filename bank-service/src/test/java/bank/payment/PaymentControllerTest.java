//package bank.payment;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import org.testcontainers.containers.GenericContainer;
//import practice.bank.BankApplication;
//import practice.bank.payment.PaymentEntity;
//import practice.bank.payment.PaymentRepository;
//import practice.bank.payment.PaymentResultPayload;
//import testdata.TestData;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class PaymentControllerTest {
//
//    @Autowired
//    private WebApplicationContext context;
//    private MockMvc mvc;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    private final String validIbanAccount = "AT611904300234573201";
//
//    static {
//        final int port = 5672;
//        final GenericContainer rabbitMq = new GenericContainer("rabbitmq:3-management").withExposedPorts(port);
//        rabbitMq.start();
//
//        System.setProperty("spring.rabbitmq.host", rabbitMq.getContainerIpAddress());
//        System.setProperty("spring.rabbitmq.port", rabbitMq.getMappedPort(port).toString());
//    }
//
//
//    @BeforeEach
//    public void setup() {
//        mvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .build();
//    }
//
//
//    @Test
//    void processingPayment_Succeed() throws Exception {
//        // given
//        PaymentResultPayload payload = TestData.paymentResultPayload(validIbanAccount, true);
//
//
//        // when
//        performPostRequest(201, payload);
//
//
//        // then
//        PaymentEntity paymentEntity = paymentRepository.findByOrderUUID(payload.getOrderUUID());
//
//        assertAll(
//                () -> assertThat(paymentEntity)
//                        .usingRecursiveComparison()
//                        .ignoringFields("id", "paymentUUID", "processingDate", "orderPrice")
//                        .isEqualTo(payload),
//                () -> assertEquals(
//                        paymentEntity.getOrderPrice().stripTrailingZeros(),
//                        payload.getOrderPrice().stripTrailingZeros()),
//                () -> assertNotNull(paymentEntity.getPaymentUUID()),
//                () -> assertTrue(paymentEntity.getIsPaymentSuccess())
//        );
//    }
//
//    @Test
//    void processingPayment_Failed_IfIsSuccessFieldIsFalse() throws Exception {
//        // given
//        PaymentResultPayload payload = TestData.paymentResultPayload(validIbanAccount, false);
//
//
//        // when
//        performPostRequest(418, payload);
//
//
//        // then
//        PaymentEntity paymentEntity = paymentRepository.findByOrderUUID(payload.getOrderUUID());
//
//        assertAll(
//                () -> assertThat(paymentEntity)
//                        .usingRecursiveComparison()
//                        .ignoringFields("id", "paymentUUID", "processingDate", "orderPrice")
//                        .isEqualTo(payload),
//                () -> assertEquals(
//                        paymentEntity.getOrderPrice().stripTrailingZeros(),
//                        payload.getOrderPrice().stripTrailingZeros()),
//                () -> assertNotNull(paymentEntity.getPaymentUUID()),
//                () -> assertFalse(paymentEntity.getIsPaymentSuccess())
//        );
//    }
//
//    @Test
//    void processingPayment_Failed_IfIbanIsInvalid() throws Exception {
//        // given
//        PaymentResultPayload payload = TestData.paymentResultPayload("invalid IBAN format", true);
//
//
//        // when
//        performPostRequest(418, payload);
//
//
//        // then
//        PaymentEntity paymentEntity = paymentRepository.findByOrderUUID(payload.getOrderUUID());
//
//        assertAll(
//                () -> assertThat(paymentEntity)
//                        .usingRecursiveComparison()
//                        .ignoringFields("id", "paymentUUID", "processingDate", "orderPrice", "isPaymentSuccess")
//                        .isEqualTo(payload),
//                () -> assertEquals(
//                        paymentEntity.getOrderPrice().stripTrailingZeros(),
//                        payload.getOrderPrice().stripTrailingZeros()),
//                () -> assertNotNull(paymentEntity.getPaymentUUID()),
//                () -> assertFalse(paymentEntity.getIsPaymentSuccess())
//        );
//    }
//
//
//    private void performPostRequest(int expectStatusCode, PaymentResultPayload payload) throws Exception {
//        mvc
//                .perform(MockMvcRequestBuilders
//                        .post("/api/payment/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(payload)))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().is(expectStatusCode));
//    }
//}

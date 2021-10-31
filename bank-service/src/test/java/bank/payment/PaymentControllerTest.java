package bank.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import practice.bank.BankApplication;
import practice.bank.payment.PaymentEntity;
import practice.bank.payment.PaymentRepository;
import practice.bank.payment.PaymentResultPayload;
import practice.bank.utils.GenerateRandomString;
import testdata.TestData;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BankApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mvc;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private GenerateRandomString generateRandomString;

    private final String MAIN_ENDPOINT = "/api/payment/";


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }


    @Test
    void processing_payment_with_success() throws Exception {
        // given
        LocalDateTime localDateTimeSendPayload = LocalDateTime.now();
        PaymentResultPayload payload = TestData.paymentResultPayload("AT611904300234573201", true);


        // when
        mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(201));


        // then
        PaymentEntity paymentEntity = paymentRepository.findByOrderUUID(payload.getOrderUUID());

        assertThat(paymentEntity)
                .usingRecursiveComparison()
                .ignoringFields("id", "paymentUUID", "processingDate", "orderPrice")
                .isEqualTo(payload);

        assertEquals(
                paymentEntity.getOrderPrice().stripTrailingZeros(),
                payload.getOrderPrice().stripTrailingZeros());

        assertNotNull(paymentEntity.getPaymentUUID());
        assertTrue(paymentEntity.getProcessingDate().isAfter(localDateTimeSendPayload));
        assertTrue(paymentEntity.getIsPaymentSuccess());
    }

    @Test
    void processing_payment_with_fail_when_isSuccess_field_is_false() throws Exception {
        // given
        LocalDateTime localDateTimeSendPayload = LocalDateTime.now();
        PaymentResultPayload payload = TestData.paymentResultPayload("AT611904300234573201", false);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(418))
                .andReturn();


        // then
        PaymentEntity paymentEntity = paymentRepository.findByOrderUUID(payload.getOrderUUID());

        assertThat(paymentEntity)
                .usingRecursiveComparison()
                .ignoringFields("id", "paymentUUID", "processingDate", "orderPrice")
                .isEqualTo(payload);

        assertEquals(
                paymentEntity.getOrderPrice().stripTrailingZeros(),
                payload.getOrderPrice().stripTrailingZeros());

        assertNotNull(paymentEntity.getPaymentUUID());
        assertTrue(paymentEntity.getProcessingDate().isAfter(localDateTimeSendPayload));
        assertFalse(paymentEntity.getIsPaymentSuccess());
    }

    @Test
    void processing_payment_with_fail_when_account_number_is_not_iban_valid() throws Exception {
        // given
        LocalDateTime localDateTimeSendPayload = LocalDateTime.now();
        PaymentResultPayload payload = TestData.paymentResultPayload("invalid IBAN format", true);


        // when
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders
                        .post(MAIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(418))
                .andReturn();


        // then
        PaymentEntity paymentEntity = paymentRepository.findByOrderUUID(payload.getOrderUUID());

        assertThat(paymentEntity)
                .usingRecursiveComparison()
                .ignoringFields("id", "paymentUUID", "processingDate", "orderPrice", "isPaymentSuccess")
                .isEqualTo(payload);

        assertEquals(
                paymentEntity.getOrderPrice().stripTrailingZeros(),
                payload.getOrderPrice().stripTrailingZeros());

        assertNotNull(paymentEntity.getPaymentUUID());
        assertTrue(paymentEntity.getProcessingDate().isAfter(localDateTimeSendPayload));
        assertFalse(paymentEntity.getIsPaymentSuccess());
    }
}

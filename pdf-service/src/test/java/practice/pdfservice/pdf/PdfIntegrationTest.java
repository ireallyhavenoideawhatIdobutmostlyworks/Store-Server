package practice.pdfservice.pdf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import practice.pdfservice.rabbit.mail.SenderMailPayload;
import practice.pdfservice.rabbit.store.ConsumerStorePayload;
import practice.pdfservice.testData.TestData;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@PropertySource({"classpath:file.properties"})
@RabbitListenerTest(capture = true)
@Tag("Integration_Test")
@SpringBootTest
public class PdfIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitListenerTestHarness harness;

    @Value("${exchange.from.store.to.pdf}")
    private String exchangeFromStoreToPdf;
    @Value("${routing.key.from.store.to.pdf}")
    private String routingKeyFromStoreToPdf;

    @Value("${docs.folder.path}")
    private String docsFolderPath;


    @BeforeAll
    public static void setGreenMail() {
        runRabbitMqContainer();
    }

    @BeforeEach
    public void setup() {
        MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @AfterEach
    void clear() throws IOException {
        FileUtils.cleanDirectory(new File(docsFolderPath));
    }


    @Test
    @Disabled
    void receiveOrderDetails_createInvoiceAndSendToEmailModule_succeeded() throws Exception {
        // given
        String fileName = UUID.randomUUID().toString();
        ConsumerStorePayload consumerStorePayload = TestData.consumerStorePayload(fileName);


        // when
        rabbitTemplate.convertAndSend(exchangeFromStoreToPdf, routingKeyFromStoreToPdf, consumerStorePayload);


        // then
        ConsumerStorePayload consumerBankListenerPayload = (ConsumerStorePayload) listenerInvocation("store", 10, SECONDS);
        SenderMailPayload senderMailPayload = queueMailBody();
        String sendFileName = persistPdfInvoice(senderMailPayload.getFileData(), senderMailPayload.getOrderUUID());

        assertAll(
                () -> assertEquals(consumerBankListenerPayload.getCustomerPdfDetails().getEmail(), senderMailPayload.getEmail()),
                () -> assertEquals(consumerBankListenerPayload.getOrderPdfDetails().getOrderUUID(), senderMailPayload.getOrderUUID()),
                () -> assertEquals(consumerBankListenerPayload.getOrderPdfDetails().getOrderUUID(), sendFileName)
        );
    }


    private Object listenerInvocation(String listenerID, long wait, TimeUnit unit) throws InterruptedException {
        RabbitListenerTestHarness.InvocationData invocationData = harness.getNextInvocationDataFor(listenerID, wait, unit);
        return invocationData.getArguments()[0];
    }

    private SenderMailPayload queueMailBody() throws JsonProcessingException {
        String queueBody = new String(Objects.requireNonNull(rabbitTemplate.receive("queue.from.pdf.to.email")).getBody(), StandardCharsets.UTF_8);
        return new ObjectMapper().readValue(queueBody, SenderMailPayload.class);
    }

    private String persistPdfInvoice(byte[] invoiceAsByte, String orderUuid) throws IOException {
        FileUtils.cleanDirectory(new File(docsFolderPath));
        File file = new File(String.format("%s/%s.pdf", docsFolderPath, orderUuid));
        FileUtils.writeByteArrayToFile(file, invoiceAsByte);
        return file.getName().replace(".pdf", "");
    }

    private static void runRabbitMqContainer() {
        int port = 5672;
        final GenericContainer rabbitMq = new GenericContainer("rabbitmq:3-management").withExposedPorts(port);
        rabbitMq.start();
        System.setProperty("spring.rabbitmq.host", rabbitMq.getContainerIpAddress());
        System.setProperty("spring.rabbitmq.port", rabbitMq.getMappedPort(port).toString());
    }
}

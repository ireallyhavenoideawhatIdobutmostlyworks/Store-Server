package practice.mailservice.mail;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import practice.mailservice.config.RabbitMqConfigTest;
import practice.mailservice.rabbit.payloads.bank.ConsumerBankPayload;
import practice.mailservice.rabbit.payloads.pdf.ConsumerPdfPayload;
import practice.mailservice.rabbit.payloads.store.ConsumerStorePayload;
import practice.mailservice.testdata.TestData;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = RabbitMqConfigTest.class)
@PropertySource("classpath:rabbitMail.properties")
@RabbitListenerTest(capture = true)
class MailIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitListenerTestHarness harness;

    @Value("${exchange.from.bank.to.email}")
    private String exchangeFromBankToEmail;
    @Value("${routing.key.from.bank.to.email}")
    private String routingKeyFromBankToEmail;

    @Value("${exchange.from.pdf.to.email}")
    private String exchangeFromPdfToEmail;
    @Value("${routing.key.from.pdf.to.email}")
    private String routingKeyFromPdfToEmail;

    @Value("${exchange.from.store.to.email}")
    private String exchangeFromStoreToEmail;
    @Value("${routing.key.from.store.to.email}")
    private String routingKeyFromStoreToEmail;

    private final String senderEmail = "your@awesome.store";
    private static GreenMail greenMail;

    @BeforeAll
    public static void setGreenMail() {
        runRabbitMqContainer();
        runMailHogContainer();
        runGreenMail();
    }

    @AfterEach
    public void setDown() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @BeforeEach
    public void setup() {
        MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }


    @Test
    void sendEmail_basedOnDataFromBank_succeed() throws Exception {
        // given
        ConsumerBankPayload consumerBankPayload = TestData.consumerBankPayload();


        // when
        rabbitTemplate.convertAndSend(exchangeFromBankToEmail, routingKeyFromBankToEmail, consumerBankPayload);


        // then
        ConsumerBankPayload consumerBankListenerPayload = (ConsumerBankPayload) listenerInvocation("bank", 10, SECONDS);

        Message[] messages = greenMail.getReceivedMessages();
        String senderReceivedMessage = messages[0].getFrom()[0].toString();
        String recipientReceivedMessage = messages[0].getAllRecipients()[0].toString();
        String subjectReceivedMessage = messages[0].getSubject();
        String contentReceivedMessage = mailContent(messages[0]);

        String recipientEmail = consumerBankPayload.getEmail();
        String subjectEmail = String.format("Status for order %s", consumerBankPayload.getOrderUUID());
        String contentEmail = String.format(
                "Status for order. Details %s %s %b",
                consumerBankPayload.getOrderUUID(),
                consumerBankPayload.getPaymentType().toString(),
                consumerBankPayload.getIsPaymentSuccess()
        );

        assertAll(
                () -> assertTrue(greenMail.waitForIncomingEmail(5000, 1)),

                () -> assertEquals(senderReceivedMessage, senderEmail),
                () -> assertEquals(recipientReceivedMessage, recipientEmail),
                () -> assertEquals(subjectReceivedMessage, subjectEmail),
                () -> assertEquals(contentReceivedMessage, contentEmail),

                () -> assertEquals(consumerBankListenerPayload, consumerBankPayload)
        );
    }

    @Test
    void sendEmail_basedOnDataFromPdf_succeed() throws Exception {
        // given
        ConsumerPdfPayload consumerPdfPayload = TestData.consumerPdfPayload("src/test/java/practice/mailservice/testfiles/%s.pdf", "inputTestFile");


        // when
        rabbitTemplate.convertAndSend(exchangeFromPdfToEmail, routingKeyFromPdfToEmail, consumerPdfPayload);


        // then
        ConsumerPdfPayload consumerPdfListenerPayload = (ConsumerPdfPayload) listenerInvocation("pdf", 10, SECONDS);

        Message[] messages = greenMail.getReceivedMessages();
        String senderReceivedMessage = messages[0].getFrom()[0].toString();
        String recipientReceivedMessage = messages[0].getAllRecipients()[0].toString();
        String subjectReceivedMessage = messages[0].getSubject();
        String contentReceivedMessage = mailContent(messages[0]);
        int attachmentReceivedMessageAmount = mailAttachmentName(messages[0]);

        String recipientEmail = consumerPdfPayload.getEmail();
        String subjectEmail = String.format("Invoice for order %s", consumerPdfPayload.getOrderUUID());
        String contentEmail = String.format("Invoice for order. Details %s", consumerPdfPayload.getOrderUUID());


        assertAll(
                () -> assertTrue(greenMail.waitForIncomingEmail(5000, 1)),

                () -> assertEquals(senderReceivedMessage, senderEmail),
                () -> assertEquals(recipientReceivedMessage, recipientEmail),
                () -> assertEquals(subjectReceivedMessage, subjectEmail),
                () -> assertEquals(contentReceivedMessage, contentEmail),
                () -> assertEquals(1, attachmentReceivedMessageAmount),

                () -> assertEquals(consumerPdfListenerPayload, consumerPdfPayload)
        );
    }

    @Test
    void sendEmail_basedOnDataFromStore_succeed() throws Exception {
        // given
        ConsumerStorePayload consumerStorePayload = TestData.consumerStorePayload();


        // when
        rabbitTemplate.convertAndSend(exchangeFromStoreToEmail, routingKeyFromStoreToEmail, consumerStorePayload);


        // then
        ConsumerStorePayload consumerStoreListenerPayload = (ConsumerStorePayload) listenerInvocation("store", 10, SECONDS);

        Message[] messages = greenMail.getReceivedMessages();
        String senderReceivedMessage = messages[0].getFrom()[0].toString();
        String recipientReceivedMessage = messages[0].getAllRecipients()[0].toString();
        String subjectReceivedMessage = messages[0].getSubject();
        String contentReceivedMessage = mailContent(messages[0]);

        String recipientEmail = consumerStorePayload.getEmail();
        String subjectEmail = String.format("New order %s", consumerStorePayload.getOrderUUID());
        String contentEmail = String.format(
                "Status for order. Details %s %s %.2f %s",
                consumerStorePayload.getOrderUUID(),
                consumerStorePayload.getPaymentUUID(),
                consumerStorePayload.getOrderPrice(),
                consumerStorePayload.getAccountNumber()
        );

        assertAll(
                () -> assertTrue(greenMail.waitForIncomingEmail(5000, 1)),

                () -> assertEquals(senderReceivedMessage, senderEmail),
                () -> assertEquals(recipientReceivedMessage, recipientEmail),
                () -> assertEquals(subjectReceivedMessage, subjectEmail),
                () -> assertEquals(contentReceivedMessage, contentEmail),

                () -> assertEquals(consumerStoreListenerPayload, consumerStorePayload)
        );
    }


    private String mailContent(Message messages) throws Exception {
        return new MimeMessageParser((MimeMessage) messages).parse().getPlainContent();
    }

    private int mailAttachmentName(Message messages) throws Exception {
        return new MimeMessageParser((MimeMessage) messages).parse().getAttachmentList().size();
    }

    private Object listenerInvocation(String listenerID, long wait, TimeUnit unit) throws InterruptedException {
        RabbitListenerTestHarness.InvocationData invocationData = harness.getNextInvocationDataFor(listenerID, wait, unit);
        return invocationData.getArguments()[0];
    }

    private static void runRabbitMqContainer() {
        int port = 5672;
        final GenericContainer rabbitMq = new GenericContainer("rabbitmq:3-management").withExposedPorts(port);
        rabbitMq.start();
        System.setProperty("spring.rabbitmq.host", rabbitMq.getContainerIpAddress());
        System.setProperty("spring.rabbitmq.port", rabbitMq.getMappedPort(port).toString());
    }

    private static void runMailHogContainer() {
        final GenericContainer mail = new GenericContainer("mailhog/mailhog")
                .withEnv("host", "localhost")
                .withEnv("port", "8025");
        mail.start();
    }

    private static void runGreenMail() {
        greenMail = new GreenMail(new ServerSetup(1025, "localhost", "smtp"));
        greenMail.start();
    }
}

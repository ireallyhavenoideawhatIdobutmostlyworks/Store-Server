package practice.mailservice.mail;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;
import practice.mailservice.mail.strategy.*;
import practice.mailservice.rabbit.payloads.bank.ConsumerBankPayload;
import practice.mailservice.rabbit.payloads.pdf.ConsumerPdfPayload;
import practice.mailservice.rabbit.payloads.store.ConsumerStorePayload;
import practice.mailservice.testdata.TestData;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Tests for mail service")
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;
    private MimeMessageHelper helper;

    private MailStrategyFactory mailStrategyFactory;

    private MailBank mailBank;
    private MailStore mailStore;
    private MailPdf mailPdf;

    private final String mailAddress = "your@awesome.store";
    private final String mailSubjectNewOrder = "New order %s";
    private final String mailContentNewOrder = "Status for order. Details %s %s %.2f %s";
    private final String mailSubjectStatusOrder = "Status for order %s";
    private final String mailContentStatusOrder = "Status for order. Details %s %s %b";
    private final String mailSubjectInvoice = "Invoice for order %s";
    private final String mailContentInvoice = "Invoice for order. Details %s";

    private static final String outputDir = "src/test/java/practice/mailservice/testfiles/";
    private final String outputPdfPath = outputDir + "%s.pdf";
    private String fileName;


    @BeforeEach
    void setUp() throws IOException {
        mailBank = new MailBank(javaMailSender);
        mailStore = new MailStore(javaMailSender);
        mailPdf = new MailPdf(javaMailSender);
        mailStrategyFactory = new MailStrategyFactory(mailBank, mailStore, mailPdf);

        setValueForFields();
        FileUtils.cleanDirectory(new File(outputDir));
        createTestFile();
    }


    @DisplayName("Check if throw exception about unknown mail type")
    @Test
    void getStrategy_unknownType_throwIllegalArgumentException() {
        // given
        MailType mailType = MailType.FAKE;


        // when
        Throwable exception = catchThrowable(() -> mailStrategyFactory.getStrategy(mailType));


        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("Unknown type: %s", mailType));
    }

    @DisplayName("Get instance of MailBank class from strategy factor")
    @Test
    void getStrategy_mailBankType_instanceOfMailBankClass() {
        // when
        MailStrategy mailStrategy = mailStrategyFactory.getStrategy(MailType.BANK);


        // then
        assertThat(mailStrategy).isInstanceOf(MailBank.class);
    }

    @DisplayName("Get instance of StoreBank class from strategy factor")
    @Test
    void getStrategy_mailStoreType_instanceOfMailStoreClass() {
        // when
        MailStrategy mailStrategy = mailStrategyFactory.getStrategy(MailType.STORE);


        // then
        assertThat(mailStrategy).isInstanceOf(MailStore.class);
    }

    @DisplayName("Get instance of PdfBank class from strategy factor")
    @Test
    void getStrategy_mailPdfType_instanceOfMailPdfClass() {
        // when
        MailStrategy mailStrategy = mailStrategyFactory.getStrategy(MailType.PDF);


        // then
        assertThat(mailStrategy).isInstanceOf(MailPdf.class);
    }

    @DisplayName("Send email with data from Bank-Service")
    @Test
    void sendEmail_basedOnDataFromBank_succeed() throws MessagingException {
        // given
        setMimeMessageDetails();
        ConsumerBankPayload consumerBankPayload = TestData.consumerBankPayload(fileName);
        String subject = String.format(mailSubjectStatusOrder, consumerBankPayload.getOrderUUID());
        String content = String.format(mailContentStatusOrder, consumerBankPayload.getOrderUUID(), consumerBankPayload.getPaymentType().toString(), consumerBankPayload.getIsPaymentSuccess());
        setEmailDetails(helper, consumerBankPayload.getEmail(), subject, content);


        // when
        mailBank.sendEmail(consumerBankPayload);


        // then
        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        assertAll(
                () -> verify(javaMailSender, times(1)).send(argument.capture()),

                () -> assertEquals(parser(argument).getFrom(), mailAddress),
                () -> assertEquals(parser(argument).getTo().get(0).toString(), consumerBankPayload.getEmail()),
                () -> assertEquals(parser(argument).getSubject(), subject),
                () -> assertEquals(parser(argument).getPlainContent(), content)
        );

    }

    @DisplayName("Send email with data from Pdf-Service")
    @Test
    void sendEmail_basedOnDataFromPdf_succeed() throws IOException, MessagingException {
        // given
        setMimeMessageDetails();
        ConsumerPdfPayload consumerPdfPayload = TestData.consumerPdfPayload(outputPdfPath, fileName);
        String subject = String.format(mailSubjectInvoice, consumerPdfPayload.getOrderUUID());
        String content = String.format(mailContentInvoice, consumerPdfPayload.getOrderUUID());
        addAttachment(consumerPdfPayload.getOrderUUID(), consumerPdfPayload.getFileData());
        setEmailDetails(helper, consumerPdfPayload.getEmail(), subject, content);


        // when
        mailPdf.sendEmail(consumerPdfPayload);


        // then
        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        assertAll(
                () -> verify(javaMailSender, times(1)).send(argument.capture()),

                () -> assertEquals(parser(argument).getFrom(), mailAddress),
                () -> assertEquals(parser(argument).getTo().get(0).toString(), consumerPdfPayload.getEmail()),
                () -> assertEquals(parser(argument).getSubject(), subject),
                () -> assertEquals(parser(argument).getPlainContent(), content),
                () -> assertEquals(parser(argument).getAttachmentList().size(), 1),
                () -> assertEquals(parser(argument).getAttachmentList().get(0).getName(), String.format("%s.pdf", consumerPdfPayload.getOrderUUID())),
                () -> assertEquals(parser(argument).getAttachmentList().get(0).getContentType(), "application/pdf")
        );
    }

    @DisplayName("Send email with data from Store-Service")
    @Test
    void sendEmail_basedOnDataFromStore_succeed() throws MessagingException {
        // given
        setMimeMessageDetails();
        ConsumerStorePayload consumerStorePayload = TestData.consumerStorePayload();
        String subject = String.format(mailSubjectNewOrder, consumerStorePayload.getOrderUUID());
        String content = String.format(mailContentNewOrder, consumerStorePayload.getOrderUUID(), consumerStorePayload.getPaymentUUID(), consumerStorePayload.getOrderPrice(), consumerStorePayload.getAccountNumber());
        setEmailDetails(helper, consumerStorePayload.getEmail(), subject, content);


        // when
        mailStore.sendEmail(consumerStorePayload);


        // then
        ArgumentCaptor<MimeMessage> argument = ArgumentCaptor.forClass(MimeMessage.class);
        assertAll(
                () -> verify(javaMailSender, times(1)).send(argument.capture()),

                () -> assertEquals(parser(argument).getFrom(), mailAddress),
                () -> assertEquals(parser(argument).getTo().get(0).toString(), consumerStorePayload.getEmail()),
                () -> assertEquals(parser(argument).getSubject(), subject),
                () -> assertEquals(parser(argument).getPlainContent(), content)
        );
    }


    private MimeMessageParser parser(ArgumentCaptor<MimeMessage> argument) throws Exception {
        MimeMessage mailContent = argument.getValue();
        MimeMessageParser parser = new MimeMessageParser(mailContent);
        return parser.parse();
    }

    private void setMimeMessageDetails() throws MessagingException {
        MimeMessage mail = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mail);
        helper = new MimeMessageHelper(mail, true);
    }

    private void addAttachment(String orderUUID, byte[] fileData) throws MessagingException, IOException {
        String pathToInvoice = String.format(outputPdfPath, orderUUID);
        FileUtils.writeByteArrayToFile(new File(pathToInvoice), fileData);

        helper.addAttachment(orderUUID, new File(pathToInvoice));
    }

    private void setEmailDetails(MimeMessageHelper helper, String email, String subject, String content) throws MessagingException {
        helper.setTo(email);
        helper.setFrom(mailAddress);
        helper.setSubject(subject);
        helper.setText(content, false);
    }

    private void setValueForFields() {
        ReflectionTestUtils.setField(mailStore, "mailAddress", mailAddress);
        ReflectionTestUtils.setField(mailStore, "mailSubjectNewOrder", mailSubjectNewOrder);
        ReflectionTestUtils.setField(mailStore, "mailContentNewOrder", mailContentNewOrder);

        ReflectionTestUtils.setField(mailBank, "mailAddress", mailAddress);
        ReflectionTestUtils.setField(mailBank, "mailSubjectStatusOrder", mailSubjectStatusOrder);
        ReflectionTestUtils.setField(mailBank, "mailContentStatusOrder", mailContentStatusOrder);

        ReflectionTestUtils.setField(mailPdf, "mailAddress", mailAddress);
        ReflectionTestUtils.setField(mailPdf, "mailSubjectInvoice", mailSubjectInvoice);
        ReflectionTestUtils.setField(mailPdf, "mailContentInvoice", mailContentInvoice);
        ReflectionTestUtils.setField(mailPdf, "outputPdfPath", outputPdfPath);
    }

    private void createTestFile() throws IOException {
        fileName = UUID.randomUUID().toString();
        String filePath = String.format(outputPdfPath, fileName);
        new File(filePath).createNewFile();
    }
}

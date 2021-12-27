package practice.mailservice.mail.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Log4j2
public class MailStrategyFactory {

    private final MailBank mailBank;
    private final MailStore mailStore;
    private final MailPdf mailPdf;

    // ToDo upgrade java to v14 (or higher) and switch switch to new switch approach

    public MailStrategy getStrategy(MailType mailType) {
        switch (mailType) {
            case BANK:
                log.info("MailType: BANK");
                return mailBank;
            case STORE:
                log.info("MailType: STORE");
                return mailStore;
            case PDF:
                log.info("MailType: PDF");
                return mailPdf;
            default:
                throw new IllegalArgumentException(String.format("Unknown type: %s", mailType));
        }
    }
}

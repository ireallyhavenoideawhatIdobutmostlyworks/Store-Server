package practice.pdfservice.rabbit.store;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.pdfservice.invoice.InvoiceService;

import java.io.IOException;

@Service
@Data
@Log4j2
public class ConsumerStoreService {

    @Autowired
    private InvoiceService invoiceService;


    @RabbitListener(queues = "${queue.from.store.to.pdf}")
    public void receivedMessage(ConsumerStorePayload consumerStorePayload) throws IOException {
        log.info("Consume storePayload object from store-service. Payload: {}", consumerStorePayload);
        invoiceService.create(consumerStorePayload);
    }
}

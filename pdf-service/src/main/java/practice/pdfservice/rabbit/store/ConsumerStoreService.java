package practice.pdfservice.rabbit.store;

import lombok.Data;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.pdfservice.invoice.InvoiceService;

import java.io.IOException;

@Service
@Data
public class ConsumerStoreService {

    @Autowired
    private InvoiceService invoiceService;
    private ConsumerStorePayload consumerStorePayload;


    @RabbitListener(queues = "${queue.from.store.to.pdf}")
    public void receivedMessage(ConsumerStorePayload consumerStorePayload) throws IOException {
        invoiceService.create(consumerStorePayload);
    }
}

package practice.store.order.rabbit.pdfService;

import lombok.Builder;
import lombok.Data;
import practice.store.order.rabbit.pdfService.detaills.CustomerPdfDetails;
import practice.store.order.rabbit.pdfService.detaills.OrderPdfDetails;
import practice.store.order.rabbit.pdfService.detaills.ProductPdfDetails;

import java.util.List;

@Builder
@Data
public class PublisherPdfPayload {

    private CustomerPdfDetails customerPdfDetails;
    private OrderPdfDetails orderPdfDetails;
    private List<ProductPdfDetails> productPdfDetailsList;
}

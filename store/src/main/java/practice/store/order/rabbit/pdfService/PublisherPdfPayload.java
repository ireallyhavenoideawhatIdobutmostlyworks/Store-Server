package practice.store.order.rabbit.pdfService;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import practice.store.order.rabbit.pdfService.detaills.CustomerPdfDetails;
import practice.store.order.rabbit.pdfService.detaills.OrderPdfDetails;
import practice.store.order.rabbit.pdfService.detaills.ProductPdfDetails;

import java.util.List;

@Builder
public class PublisherPdfPayload {

    @JsonProperty
    private CustomerPdfDetails customerPdfDetails;
    @JsonProperty
    private OrderPdfDetails orderPdfDetails;
    @JsonProperty
    private List<ProductPdfDetails> productPdfDetailsList;
}

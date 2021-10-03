package practice.store.rabbit.services.pdf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import practice.store.rabbit.services.pdf.detaills.CustomerPdfDetails;
import practice.store.rabbit.services.pdf.detaills.OrderPdfDetails;
import practice.store.rabbit.services.pdf.detaills.ProductPdfDetails;

import java.util.List;

@Builder
public class SenderPdfPayload {

    @JsonProperty
    private CustomerPdfDetails customerPdfDetails;
    @JsonProperty
    private OrderPdfDetails orderPdfDetails;
    @JsonProperty
    private List<ProductPdfDetails> productPdfDetailsList;
}

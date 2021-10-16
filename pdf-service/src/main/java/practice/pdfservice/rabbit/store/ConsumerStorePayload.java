package practice.pdfservice.rabbit.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import practice.pdfservice.rabbit.store.detaills.CustomerPdfDetails;
import practice.pdfservice.rabbit.store.detaills.OrderPdfDetails;
import practice.pdfservice.rabbit.store.detaills.ProductPdfDetails;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerStorePayload {

    private CustomerPdfDetails customerPdfDetails;
    private OrderPdfDetails orderPdfDetails;
    private List<ProductPdfDetails> productPdfDetailsList;
}

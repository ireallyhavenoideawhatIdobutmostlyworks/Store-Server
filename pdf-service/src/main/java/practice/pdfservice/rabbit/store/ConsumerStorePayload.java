package practice.pdfservice.rabbit.store;

import lombok.*;
import practice.pdfservice.rabbit.store.detaills.CustomerPdfDetails;
import practice.pdfservice.rabbit.store.detaills.OrderPdfDetails;
import practice.pdfservice.rabbit.store.detaills.ProductPdfDetails;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
public class ConsumerStorePayload {

    private CustomerPdfDetails customerPdfDetails;
    private OrderPdfDetails orderPdfDetails;
    private List<ProductPdfDetails> productPdfDetailsList;
}

package practice.pdfservice.rabbit.store.detaills;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPdfDetails {

    private String name;
    private String productUUID;
    private String description;
    private BigDecimal finalPrice;
}

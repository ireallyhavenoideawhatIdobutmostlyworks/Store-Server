package practice.pdfservice.rabbit.store.detaills;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
public class ProductPdfDetails {

    private String name;
    private String productUUID;
    private String description;
    private BigDecimal finalPrice;
}

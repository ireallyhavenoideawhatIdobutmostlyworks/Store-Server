package practice.store.order.rabbit.pdfService.detaills;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class ProductPdfDetails {

    private String name;
    private String productUUID;
    private String description;
    private BigDecimal finalPrice;
}

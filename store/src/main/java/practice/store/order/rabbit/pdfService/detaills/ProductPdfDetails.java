package practice.store.order.rabbit.pdfService.detaills;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class ProductPdfDetails {

    @JsonProperty
    private String name;
    @JsonProperty
    private String productUUID;
    @JsonProperty
    private String description;
    @JsonProperty
    private BigDecimal finalPrice;
}

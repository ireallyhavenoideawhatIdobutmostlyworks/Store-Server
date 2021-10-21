package practice.store.rabbit.services.pdf.detaills;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
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

package practice.store.rabbit.services.pdf.detaills;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Builder
public final class ProductPdfDetails {

    @JsonProperty
    private final String name;
    @JsonProperty
    private final String productUUID;
    @JsonProperty
    private final String description;
    @JsonProperty
    private final BigDecimal finalPrice;
}

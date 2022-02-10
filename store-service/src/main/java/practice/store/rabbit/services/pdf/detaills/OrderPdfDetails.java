package practice.store.rabbit.services.pdf.detaills;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Builder
public final class OrderPdfDetails {

    @JsonProperty
    private final String orderUUID;
    @JsonProperty
    private final String paymentUUID;
    @JsonProperty
    private final BigDecimal orderPrice;
    @JsonProperty
    private final String accountNumber;
}

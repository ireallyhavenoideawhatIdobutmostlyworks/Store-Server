package practice.store.rabbit.services.pdf.detaills;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Builder
public class OrderPdfDetails {

    @JsonProperty
    private String orderUUID;
    @JsonProperty
    private String paymentUUID;
    @JsonProperty
    private BigDecimal orderPrice;
    @JsonProperty
    private String accountNumber;
}

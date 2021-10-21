package practice.bank.rabbit.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;
import practice.bank.payment.PaymentType;

import java.math.BigDecimal;

@ToString
@Builder
public class SenderStorePayload {

    @JsonProperty
    private String orderUUID;
    @JsonProperty
    private String paymentUUID;
    @JsonProperty
    private BigDecimal orderPrice;
    @JsonProperty
    private String accountNumber;
    @JsonProperty
    private PaymentType paymentType;
    @JsonProperty
    private Boolean isPaymentSuccess;
}

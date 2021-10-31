package practice.bank.rabbit.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;
import practice.bank.payment.PaymentType;

import java.io.Serializable;
import java.math.BigDecimal;

@ToString
@Builder
public class SenderStorePayload implements Serializable {

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

package practice.bank.rabbit.mail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import practice.bank.payment.PaymentType;

import java.math.BigDecimal;

@Builder
@Data
public class SenderMailPayload {

    @JsonProperty
    private String orderUUID;
    @JsonProperty
    private String paymentUUID;
    @JsonProperty
    private BigDecimal orderPrice;
    @JsonProperty
    private String accountNumber;
    @JsonProperty
    private String email;
    @JsonProperty
    private PaymentType paymentType;
    @JsonProperty
    private Boolean isPaymentSuccess;
}

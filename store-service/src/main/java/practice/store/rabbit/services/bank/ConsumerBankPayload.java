package practice.store.rabbit.services.bank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import practice.store.order.PaymentType;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class ConsumerBankPayload {

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

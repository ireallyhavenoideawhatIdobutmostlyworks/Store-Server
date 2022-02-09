package practice.store.rabbit.services.bank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import practice.store.order.PaymentType;

import java.math.BigDecimal;

@Builder
public final class ConsumerBankPayload {

    @JsonProperty
    private final String orderUUID;
    @JsonProperty
    private final String paymentUUID;
    @JsonProperty
    private final BigDecimal orderPrice;
    @JsonProperty
    private final String accountNumber;
    @JsonProperty
    private final PaymentType paymentType;
    @JsonProperty
    private final Boolean isPaymentSuccess;
}

package practice.store.rabbit.services.mail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public final class SenderMailPayload {

    @JsonProperty
    private final String orderUUID;
    @JsonProperty
    private final String paymentUUID;
    @JsonProperty
    private final BigDecimal orderPrice;
    @JsonProperty
    private final String accountNumber;
    @JsonProperty
    private final String email;
}

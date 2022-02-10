package practice.store.rabbit.services.mail;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public final class SenderMailPayload {

    private final String orderUUID;
    private final String paymentUUID;
    private final BigDecimal orderPrice;
    private final String accountNumber;
    private final String email;
}

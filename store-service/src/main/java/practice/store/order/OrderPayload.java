package practice.store.order;

import lombok.Builder;
import lombok.Getter;
import practice.store.order.details.OrderProductPayload;

import java.util.Set;

@Builder
@Getter
public final class OrderPayload {

    private final String payloadUUID;
    private final String accountNumber;
    private final PaymentType paymentType;
    private final Set<OrderProductPayload> orderProductPayloads;
}

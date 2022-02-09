package practice.store.order.details;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public final class OrderProductPayload {

    private final int amount;
    private final String productUUID;
}

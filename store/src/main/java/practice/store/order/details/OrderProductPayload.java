package practice.store.order.details;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderProductPayload {

    private int amount;
    private String productUUID;
}

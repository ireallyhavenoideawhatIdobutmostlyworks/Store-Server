package practice.store.order;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderContentPayload {

    private String orderUUID;
    private int amount;
}

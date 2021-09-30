package practice.store.order.rabbit.mailService;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class PublisherMailPayload {

    private String orderUUID;
    private String paymentUUID;
    private BigDecimal orderPrice;
    private String accountNumber;
    private String email;
}

package practice.mailservice.rabbit.store;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ConsumerStorePayload {

    private String orderUUID;
    private String paymentUUID;
    private BigDecimal orderPrice;
    private String accountNumber;
    private String email;
}

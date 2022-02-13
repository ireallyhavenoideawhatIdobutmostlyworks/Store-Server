package practice.mailservice.rabbit.payloads.store;

import lombok.*;
import practice.mailservice.rabbit.payloads.ConsumerPayload;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
public final class ConsumerStorePayload implements ConsumerPayload {

    private String orderUUID;
    private String paymentUUID;
    private BigDecimal orderPrice;
    private String accountNumber;
    private String email;
}

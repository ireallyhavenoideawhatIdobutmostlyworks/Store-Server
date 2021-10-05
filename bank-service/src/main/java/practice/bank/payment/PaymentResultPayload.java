package practice.bank.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class PaymentResultPayload {

    private String orderUUID;
    private String paymentUUID;
    private BigDecimal orderPrice;
    private String accountNumber;
    private PaymentType paymentType;
    private Boolean isPaymentSuccess; // virgin idea - checkbox in frontend to simulate behavior
}

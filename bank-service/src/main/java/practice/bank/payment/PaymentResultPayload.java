package practice.bank.payment;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class PaymentResultPayload {

    private String orderUUID;
    private BigDecimal orderPrice;
    private String accountNumber;
    private PaymentType paymentType;
    private String email;
    private Boolean isPaymentSuccess; // TODO checkbox on fronted for simulate behavior
}

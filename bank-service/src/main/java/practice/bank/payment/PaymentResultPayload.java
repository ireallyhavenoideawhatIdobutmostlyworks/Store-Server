package practice.bank.payment;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public final class PaymentResultPayload {

    private final String orderUUID;
    private final BigDecimal orderPrice;
    private final String accountNumber;
    private final PaymentType paymentType;
    private final String email;
    private final Boolean isPaymentSuccess; // TODO checkbox on fronted for simulate behavior
}

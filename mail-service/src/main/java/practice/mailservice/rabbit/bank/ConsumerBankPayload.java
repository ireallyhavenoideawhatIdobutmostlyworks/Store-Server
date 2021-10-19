package practice.mailservice.rabbit.bank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ConsumerBankPayload {

    private String orderUUID;
    private String paymentUUID;
    private BigDecimal orderPrice;
    private String accountNumber;
    private String email;
    private PaymentType paymentType;
    private Boolean isPaymentSuccess;
}

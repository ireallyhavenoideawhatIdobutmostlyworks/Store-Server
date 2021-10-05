package practice.bank.exceptions;

import practice.bank.payment.PaymentResultPayload;

public class PaymentFailureException extends RuntimeException {

    public PaymentFailureException(PaymentResultPayload payload, String paymentUUID) {
        super(String.format("Payment failure. Payment details: " +
                        "Order UUID: %s, " +
                        "payment UUID: %s" +
                        "Order price: %.2f, " +
                        "account number: %s, " +
                        "payment type: %s, ",
                payload.getOrderPrice(),
                paymentUUID,
                payload.getOrderPrice(),
                payload.getAccountNumber(),
                payload.getPaymentType().toString()
        ));
    }
}

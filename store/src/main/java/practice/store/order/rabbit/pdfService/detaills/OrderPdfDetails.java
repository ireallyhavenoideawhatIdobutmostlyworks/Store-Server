package practice.store.order.rabbit.pdfService.detaills;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public class OrderPdfDetails {

    private String orderUUID;
    private String paymentUUID;
    private BigDecimal orderPrice;
    private String accountNumber;
}

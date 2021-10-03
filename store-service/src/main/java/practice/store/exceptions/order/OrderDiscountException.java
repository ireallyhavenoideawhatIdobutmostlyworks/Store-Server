package practice.store.exceptions.order;

import java.math.BigDecimal;

public class OrderDiscountException extends RuntimeException {

    public OrderDiscountException(BigDecimal basePrice, BigDecimal finalPrice) {
        super(String.format("Prices should not be same because order has discount. Base price:%.2f, final price:%.2f", basePrice, finalPrice));
    }
}

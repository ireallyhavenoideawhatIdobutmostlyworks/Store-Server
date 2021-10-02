package practice.store.exceptions.order;

import java.math.BigDecimal;

public class OrderFinalPriceException extends RuntimeException {

    public OrderFinalPriceException() {
        super("Final price should be grater than 0.");
    }

    public OrderFinalPriceException(BigDecimal orderFinalPrice, BigDecimal allProductsPrice) {
        super(String.format(
                "Order final price and all products price should are same. Order final price: %.2f, all products price: %.2f",
                orderFinalPrice,
                allProductsPrice
        ));
    }

    public OrderFinalPriceException(BigDecimal finalPrice, BigDecimal basePrice, int discountPercentage, BigDecimal finalPriceCalculate) {
        super(String.format(
                "Incorrect final price. Final price: %.2f. Base price: %.2f. Discount percentage: %d. Correct final price:%.2f.",
                finalPrice,
                basePrice,
                discountPercentage,
                finalPriceCalculate
        ));
    }
}

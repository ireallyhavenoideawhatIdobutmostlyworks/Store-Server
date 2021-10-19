package practice.store.exceptions.product;

import java.math.BigDecimal;

public class ProductFinalPriceException extends RuntimeException {

    public ProductFinalPriceException(BigDecimal finalPrice, BigDecimal finalPriceCalculate) {
        super(String.format(
                "Incorrect final price. Final price from payload:%.2f. Correct final price:%.2f.",
                finalPrice,
                finalPriceCalculate
        ));
    }
}

package practice.store.exceptions.product;

import java.math.BigDecimal;

public class ProductPriceReductionException extends RuntimeException {

    public ProductPriceReductionException(BigDecimal amountPriceReduction, BigDecimal amountPriceReductionCalculate) {
        super(String.format(
                "Incorrect price reduction. Price reduction from payload:%.2f. Correct price reduction:%.2f.",
                amountPriceReduction,
                amountPriceReductionCalculate
        ));
    }

    public ProductPriceReductionException() {
        super("Price reduction should equal 0 because that product is without discount.");
    }
}

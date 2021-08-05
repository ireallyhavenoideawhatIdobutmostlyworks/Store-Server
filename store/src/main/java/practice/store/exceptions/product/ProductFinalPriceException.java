package practice.store.exceptions.product;

public class ProductFinalPriceException extends RuntimeException {

    public ProductFinalPriceException(double finalPrice, double finalPriceCalculate) {
        super(String.format(
                "Incorrect final price. Final price from payload:%f. Correct final price:%f.",
                finalPrice,
                finalPriceCalculate
        ));
    }
}

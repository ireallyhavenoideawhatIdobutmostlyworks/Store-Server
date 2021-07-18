package practice.store.exceptions.product;

public class ProductPriceReductionException extends RuntimeException {

    public ProductPriceReductionException(double amountPriceReduction, double amountPriceReductionCalculate) {
        super(String.format(
                "Incorrect price reduction. Price reduction from payload:%f. Correct price reduction:%f.",
                amountPriceReduction,
                amountPriceReductionCalculate
        ));
    }

    public ProductPriceReductionException() {
        super("Price reduction should equal 0.");
    }
}

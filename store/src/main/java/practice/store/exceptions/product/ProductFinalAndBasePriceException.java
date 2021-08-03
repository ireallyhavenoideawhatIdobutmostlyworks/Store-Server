package practice.store.exceptions.product;

public class ProductFinalAndBasePriceException extends RuntimeException {

    public ProductFinalAndBasePriceException() {
        super("Final price should equal base price.");
    }
}

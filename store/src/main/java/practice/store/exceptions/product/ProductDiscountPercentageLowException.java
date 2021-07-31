package practice.store.exceptions.product;

public class ProductDiscountPercentageLowException extends RuntimeException {

    public ProductDiscountPercentageLowException() {
        super("The percentage discount may not be lower than 5%.");
    }
}
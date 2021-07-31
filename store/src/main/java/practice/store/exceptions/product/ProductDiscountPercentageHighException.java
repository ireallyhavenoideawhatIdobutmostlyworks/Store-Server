package practice.store.exceptions.product;

public class ProductDiscountPercentageHighException extends RuntimeException {

    public ProductDiscountPercentageHighException() {
        super("The percentage discount may not be higher than 95%.");
    }
}
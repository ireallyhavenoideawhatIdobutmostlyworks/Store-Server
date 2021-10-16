package practice.store.exceptions.product;

public class ProductDiscountPercentageException extends RuntimeException {

    public ProductDiscountPercentageException() {
        super("Discount percentage should be equal 0 because that product is without discount.");
    }
}

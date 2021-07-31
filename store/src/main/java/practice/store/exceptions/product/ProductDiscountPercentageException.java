package practice.store.exceptions.product;

public class ProductDiscountPercentageException extends RuntimeException {

    public ProductDiscountPercentageException() {
        super("Discount percentage should equal 0.");
    }
}

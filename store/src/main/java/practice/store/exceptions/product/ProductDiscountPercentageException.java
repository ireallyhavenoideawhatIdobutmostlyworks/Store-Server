package practice.store.exceptions.product;

public class ProductDiscountPercentageException extends RuntimeException {

    public ProductDiscountPercentageException(int discount) {
        super(String.format("Discount percentage should be equal %d%%.", discount));
    }
}

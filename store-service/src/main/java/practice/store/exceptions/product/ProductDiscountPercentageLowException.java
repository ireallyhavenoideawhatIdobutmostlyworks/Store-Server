package practice.store.exceptions.product;

public class ProductDiscountPercentageLowException extends RuntimeException {

    public ProductDiscountPercentageLowException(int discount) {
        super(String.format("The percentage discount may not be lower than %d%%.", discount));
    }
}
package practice.store.exceptions.product;

public class ProductDiscountPercentageHighException extends RuntimeException {

    public ProductDiscountPercentageHighException(int discount) {
        super(String.format("The percentage discount may not be higher than %d%%.", discount));
    }
}
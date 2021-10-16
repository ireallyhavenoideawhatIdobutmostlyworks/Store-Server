package practice.store.exceptions.product;

public class ProductAmountInvalidParameterException extends RuntimeException {

    public ProductAmountInvalidParameterException(int amount, String uuid) {
        super(String.format("Invalid amount parameter:%d for product with UUID:%s.", amount, uuid));
    }
}

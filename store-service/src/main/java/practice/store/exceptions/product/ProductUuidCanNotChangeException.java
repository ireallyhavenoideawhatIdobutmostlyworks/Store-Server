package practice.store.exceptions.product;

public class ProductUuidCanNotChangeException extends RuntimeException {

    public ProductUuidCanNotChangeException() {
        super("UUID cannot be changed.");
    }
}

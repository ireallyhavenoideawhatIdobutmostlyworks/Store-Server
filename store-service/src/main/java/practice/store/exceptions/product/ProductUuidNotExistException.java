package practice.store.exceptions.product;

public class ProductUuidNotExistException extends RuntimeException {

    public ProductUuidNotExistException(String uuid) {
        super(String.format("Product with UUID:%s is not exist.", uuid));
    }
}
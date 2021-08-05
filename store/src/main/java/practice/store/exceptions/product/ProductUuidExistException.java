package practice.store.exceptions.product;

public class ProductUuidExistException extends RuntimeException {

    public ProductUuidExistException(String uuid) {
        super(String.format("Product with UUID:%s is exist.", uuid));
    }
}

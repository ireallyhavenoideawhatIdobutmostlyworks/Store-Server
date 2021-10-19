package practice.store.exceptions.product;

public class ProductWithdrawFromSaleException extends RuntimeException {

    public ProductWithdrawFromSaleException(String name, String uuid) {
        super(String.format("A product withdrawn from sale cannot be added. Name: %s, UUID: %s", name, uuid));
    }
}

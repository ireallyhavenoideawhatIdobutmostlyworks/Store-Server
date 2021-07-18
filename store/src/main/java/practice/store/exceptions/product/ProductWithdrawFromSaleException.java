package practice.store.exceptions.product;

public class ProductWithdrawFromSaleException extends RuntimeException {

    public ProductWithdrawFromSaleException() {
        super("A product withdrawn from sale cannot be added.");
    }
}

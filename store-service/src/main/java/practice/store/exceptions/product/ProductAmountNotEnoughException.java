package practice.store.exceptions.product;

public class ProductAmountNotEnoughException extends RuntimeException {

    public ProductAmountNotEnoughException() {
        super("Not enough product in stock.");
    }
}
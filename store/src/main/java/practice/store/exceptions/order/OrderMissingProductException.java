package practice.store.exceptions.order;

public class OrderMissingProductException extends RuntimeException {

    public OrderMissingProductException() {
        super("There is no product in order.");
    }
}

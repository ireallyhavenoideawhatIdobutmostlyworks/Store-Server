package practice.store.exceptions.order;

public class OrderBasePriceException extends RuntimeException {

    public OrderBasePriceException() {
        super("Base price should be grater than 0.");
    }
}

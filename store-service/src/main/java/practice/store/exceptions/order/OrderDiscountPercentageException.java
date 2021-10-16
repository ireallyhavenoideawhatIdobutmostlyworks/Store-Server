package practice.store.exceptions.order;

public class OrderDiscountPercentageException extends RuntimeException {

    public OrderDiscountPercentageException() {
        super("Discount percentage should not be equal 0 if order has discount.");
    }

    public OrderDiscountPercentageException(int discountPercentage) {
        super(String.format("Discount percentage should be equal 0 if order not has discount. Actual discount percentage is: %d", discountPercentage));
    }
}

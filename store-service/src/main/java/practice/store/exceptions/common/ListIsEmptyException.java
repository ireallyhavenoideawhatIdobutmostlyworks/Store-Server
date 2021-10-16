package practice.store.exceptions.common;

public class ListIsEmptyException extends RuntimeException {

    public ListIsEmptyException() {
        super("The list is empty. Please try later.");
    }
}

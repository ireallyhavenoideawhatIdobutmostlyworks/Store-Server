package practice.store.exceptions.common;

public class UuidCanNotChangeException extends RuntimeException {

    public UuidCanNotChangeException() {
        super("UUID cannot be changed.");
    }
}
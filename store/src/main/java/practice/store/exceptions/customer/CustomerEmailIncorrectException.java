package practice.store.exceptions.customer;

public class CustomerEmailIncorrectException extends RuntimeException {

    public CustomerEmailIncorrectException(String email, long id) {
        super(String.format("Email:%s and id:%d not belong to same customer.", email, id));
    }
}

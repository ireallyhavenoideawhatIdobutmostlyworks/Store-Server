package practice.store.exceptions.customer;

public class CustomerEmailWithIdIncorrectException extends RuntimeException {

    public CustomerEmailWithIdIncorrectException(String email, long id) {
        super(String.format("Email:%s and id:%d not belong to same customer.", email, id));
    }
}

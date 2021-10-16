package practice.store.exceptions.customer;

public class CustomerEmailNotFoundException extends RuntimeException {

    public CustomerEmailNotFoundException(String email) {
        super(String.format("Email:%s not found.", email));
    }
}

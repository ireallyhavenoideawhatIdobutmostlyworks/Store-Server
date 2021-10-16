package practice.store.exceptions.customer;

public class CustomerIsNotActiveException extends RuntimeException {

    public CustomerIsNotActiveException(String email) {
        super(String.format("Customer with email:%s is not active.", email));
    }
}

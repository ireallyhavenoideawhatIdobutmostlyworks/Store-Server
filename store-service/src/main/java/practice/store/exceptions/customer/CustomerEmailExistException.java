package practice.store.exceptions.customer;

public class CustomerEmailExistException extends RuntimeException {

    public CustomerEmailExistException(String email) {
        super(String.format("Customer with email:%s is exist.", email));
    }
}

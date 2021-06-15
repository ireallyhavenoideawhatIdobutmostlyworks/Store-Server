package practice.store.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import practice.store.exceptions.common.EntityNotFoundException;
import practice.store.exceptions.customer.CustomerEmailExistException;
import practice.store.exceptions.customer.CustomerEmailIncorrectException;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final EntitiesConverter entitiesConverter;
    private final PayloadsConverter payloadsConverter;


    public CustomerPayload getById(long id) {
        checkIfEntityExist(id);

        return entitiesConverter.convertCustomer(customerRepository.getById(id));
    }

    public List<CustomerPayload> getList() {
        return customerRepository
                .findAll()
                .stream()
                .map(entitiesConverter::convertCustomer)
                .collect(Collectors.toList());
    }

    public void save(CustomerPayload customerPayload) {
        checkIfCustomerEmailExist(customerPayload.getEmail());

        customerPayload.setId(null);

        CustomerEntity customerEntity = payloadsConverter.convertCustomer(customerPayload);
        customerRepository.save(customerEntity);
    }

    public void edit(CustomerPayload customerPayload, long id) {
        checkIfEntityExist(id);
        checkIfCustomerEmailExist(customerPayload.getEmail(), id);

        customerPayload.setId(id);

        CustomerEntity existingCustomer = payloadsConverter.convertCustomer(customerPayload);
        customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(long id) {
        checkIfEntityExist(id);

        CustomerEntity existingCustomer = customerRepository.getById(id);
        existingCustomer.setActive(false);

        customerRepository.save(existingCustomer);
    }


    private void checkIfEntityExist(long id) {
        if (!customerRepository.existsById(id))
            throw new EntityNotFoundException(getClass().getSimpleName(), id);
    }

    private void checkIfCustomerEmailExist(String email) {
        if (customerRepository.existsByEmail(email))
            throw new CustomerEmailExistException(email);
    }

    private void checkIfCustomerEmailExist(String email, long id) {
        if (!customerRepository.existsByEmailAndId(email, id))
            throw new CustomerEmailIncorrectException(email, id);
    }
}
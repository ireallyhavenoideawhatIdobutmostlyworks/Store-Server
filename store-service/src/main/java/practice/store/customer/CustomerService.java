package practice.store.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.store.exceptions.customer.CustomerEmailExistException;
import practice.store.exceptions.customer.CustomerEmailWithIdIncorrectException;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
@Log4j2
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final EntitiesConverter entitiesConverter;
    private final PayloadsConverter payloadsConverter;


    public CustomerPayload getById(long id) {
        log.info("Looking for customer by id: {}", id);
        return entitiesConverter.convertCustomer(customerRepository.getById(id));
    }

    public List<CustomerPayload> getList() {
        List<CustomerPayload> customersList = customerRepository
                .findAll()
                .stream()
                .map(entitiesConverter::convertCustomer)
                .collect(Collectors.toList());

        log.info("Looking for all customers. List size: {}", customersList.size());
        return customersList;
    }

    public void save(CustomerPayload customerPayload) {
        checkIfCustomerExistByEmail(customerPayload.getEmail());

        customerPayload.setId(null);

        CustomerEntity customerEntity = payloadsConverter.convertCustomer(customerPayload);
        customerRepository.save(customerEntity);
        log.info("Saved new customer. Entity details: {}", customerEntity);
    }

    public void edit(CustomerPayload customerPayload, long id) {
        checkIfCustomerExistByEmailAndId(customerPayload.getEmail(), id);

        customerPayload.setId(id);

        CustomerEntity existingCustomer = payloadsConverter.convertCustomer(customerPayload);
        customerRepository.save(existingCustomer);
        log.info("Edited customer. Entity details: {}", existingCustomer);
        // ToDo add new payload for edit or change existing.
    }

    public void deleteCustomer(long id) {
        CustomerEntity existingCustomer = customerRepository.getById(id);
        existingCustomer.setActive(false);

        customerRepository.save(existingCustomer);
        log.info("Marked customer as inactive. Entity details: {}", existingCustomer);
    }


    private void checkIfCustomerExistByEmail(String email) {
        if (customerRepository.existsByEmail(email))
            throw new CustomerEmailExistException(email);
    }

    private void checkIfCustomerExistByEmailAndId(String email, long id) {
        if (!customerRepository.existsByEmailAndId(email, id))
            throw new CustomerEmailWithIdIncorrectException(email, id);
    }
}
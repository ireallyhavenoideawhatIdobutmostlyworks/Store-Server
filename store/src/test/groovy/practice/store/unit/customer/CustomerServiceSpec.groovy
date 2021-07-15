package practice.store.unit.customer


import org.springframework.security.crypto.password.PasswordEncoder
import practice.DataFactoryEntities
import practice.DataFactoryPayloads
import practice.store.customer.CustomerRepository
import practice.store.customer.CustomerService
import practice.store.exceptions.customer.CustomerEmailExistException
import practice.store.exceptions.customer.CustomerEmailWithIdIncorrectException
import practice.store.utils.converter.EntitiesConverter
import practice.store.utils.converter.PayloadsConverter
import spock.lang.Shared
import spock.lang.Specification

import javax.persistence.EntityNotFoundException

class CustomerServiceSpec extends Specification {

    @Shared passwordEncoder
    @Shared customerRepository

    @Shared customerService
    @Shared customerEntity
    @Shared customerPayload

    @Shared entitiesConverter
    @Shared payloadsConverter


    def setup() {
        passwordEncoder = Mock(PasswordEncoder.class)
        customerRepository = Mock(CustomerRepository.class)

        entitiesConverter = new EntitiesConverter()
        payloadsConverter = new PayloadsConverter(passwordEncoder)

        customerService = new CustomerService(customerRepository, entitiesConverter, payloadsConverter)

        customerEntity = DataFactoryEntities.createCustomerEntity(1L, "test name", "test password", "test@email.store", true, true)
        customerPayload = DataFactoryPayloads.createCustomerPayload(1L, "test name", "test password", "test@email.store", true, true)
    }


    def "should_return_customer_when_id_is_exist_test"() {
        given:
        long id = customerEntity.getId()
        customerRepository.getById(id) >> customerEntity
        def customerPayload = entitiesConverter.convertCustomer(customerEntity)


        when:
        def customerPayloadReturnedFromService = customerService.getById(customerPayload.getId())


        then:
        assert customerPayload == customerPayloadReturnedFromService
        1 * customerRepository.getById(id) >> customerEntity
    }

    def "should_throw_exception_when_id_is_not_exist_test"() {
        given:
        String exceptionMessage = "Unable to find practice.store.customer.CustomerEntity with id %d"
        long idNotExist = 11L
        customerRepository.getById(idNotExist) >> new EntityNotFoundException(String.format(exceptionMessage, idNotExist))


        when:
        customerService.getById(idNotExist)


        then:
        def exception = thrown(EntityNotFoundException)
        assert exception.message == String.format(exceptionMessage, idNotExist)

        1 * customerRepository.getById(idNotExist) >> {
            throw new EntityNotFoundException(String.format(exceptionMessage, idNotExist))
        }
    }

    def "should_return_customers_list_when_list_is_not_empty_test"() {
        given:
        def customerEntityList = Collections.singletonList(customerEntity);
        customerRepository.findAll() >> customerEntityList
        def customerPayloadList = Collections.singletonList(entitiesConverter.convertCustomer(customerEntity))


        when:
        def customerPayloadListReturnedFromService = customerService.getList()


        then:
        assert customerPayloadList == customerPayloadListReturnedFromService

        1 * customerRepository.findAll() >> customerEntityList
    }

    def "should_return_empty_customers_list_when_list_is_empty_test"() {
        given:
        def emptyCustomerEntityList = new ArrayList<>();
        customerRepository.findAll() >> emptyCustomerEntityList


        when:
        def customerPayloadListReturnedFromService = customerService.getList()


        then:
        assert customerPayloadListReturnedFromService != null
        assert customerPayloadListReturnedFromService.isEmpty()
        1 * customerRepository.findAll() >> emptyCustomerEntityList
    }

    def "should_add_customer_when_payload_is_correct_test"() {
        given:
        customerRepository.existsByEmail(customerPayload.getEmail()) >> false
        customerPayload.setId(null)
        def customerEntity = payloadsConverter.convertCustomer(customerPayload)


        when:
        customerService.save(customerPayload);


        then:
        1 * customerRepository.save(customerEntity)
        1 * customerRepository.existsByEmail(customerPayload.getEmail()) >> false
    }

    def "should_throw_exception_when_email_is_exist_during_save_test"() {
        given:
        String emailIsExist = customerEntity.getEmail()
        String exceptionMessage = "Customer with email:%s is exist."

        customerRepository.existsByEmail(customerPayload.getEmail()) >> true


        when:
        customerService.save(customerPayload)


        then:
        def exception = thrown(CustomerEmailExistException)
        assert exception.message == String.format(exceptionMessage, emailIsExist)

        1 * customerRepository.existsByEmail(customerPayload.getEmail()) >> true
        0 * customerRepository.save(customerEntity)
    }

    def "should_edit_customer_when_payload_is_correct_test"() {
        given:
        String emailExist = customerPayload.getEmail()
        long idExist = customerPayload.getId()

        customerRepository.existsByEmailAndId(emailExist, idExist) >> true
        def existingCustomer = payloadsConverter.convertCustomer(customerPayload)


        when:
        customerService.edit(customerPayload, idExist)


        then:
        1 * customerRepository.existsByEmailAndId(emailExist, idExist) >> true
        1 * customerRepository.save(existingCustomer)
    }

    def "should_throw_exception_when_email_and_id_not_belong_to_same_customer_test"() {
        given:
        String emailExist = customerEntity.getEmail()
        long idNotExist = 11L
        String exceptionMessage = "Email:%s and id:%d not belong to same customer."


        when:
        customerService.edit(customerPayload, idNotExist)


        then:
        def exception = thrown(CustomerEmailWithIdIncorrectException)
        assert exception.message == String.format(exceptionMessage, emailExist, idNotExist)

        1 * customerRepository.existsByEmailAndId(emailExist, idNotExist) >> false
        0 * customerRepository.save(customerEntity)
    }

    def "should_set_is_active_field_to_false_test"() {
        given:
        customerRepository.getById(customerEntity.getId()) >> customerEntity
        customerEntity.setActive(false)


        when:
        customerService.deleteCustomer(customerEntity.getId())


        then:
        1 * customerRepository.getById(customerEntity.getId()) >> customerEntity
        1 * customerRepository.save(customerEntity)
    }

    def "should_throw_exception_when_id_is_not_exist_during_delete_test"() {
        given:
        String exceptionMessage = "Unable to find practice.store.customer.CustomerEntity with id %d"
        long idNotExist = 11L
        customerRepository.getById(idNotExist) >> new EntityNotFoundException(String.format(exceptionMessage, idNotExist))


        when:
        customerService.deleteCustomer(idNotExist)


        then:
        def exception = thrown(EntityNotFoundException)
        assert exception.message == String.format(exceptionMessage, idNotExist)

        1 * customerRepository.getById(idNotExist) >> {
            throw new EntityNotFoundException(String.format(exceptionMessage, idNotExist))
        }

        0 * customerRepository.save(customerEntity)
    }
}
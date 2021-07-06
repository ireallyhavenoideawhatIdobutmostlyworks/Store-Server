package practice.store.customer

import groovy.json.JsonOutput
import org.mockito.Mock
import org.springframework.security.crypto.password.PasswordEncoder
import practice.store.DataFactory
import practice.store.utils.converter.EntitiesConverter
import practice.store.utils.converter.PayloadsConverter
import spock.lang.Shared
import spock.lang.Specification


class CustomerServiceSpec extends Specification {

    @Shared
     passwordEncoder
    @Shared
     customerRepository;

    @Shared
     customerService;
    @Shared
     customerEntity;
    @Shared
     customerPayload;

    @Shared
     entitiesConverter;
    @Shared
     payloadsConverter;


    def setup() {
        passwordEncoder = Mock(PasswordEncoder.class)
        customerRepository = Mock(CustomerRepository.class)

        entitiesConverter = new EntitiesConverter();
        payloadsConverter = new PayloadsConverter(passwordEncoder);

        customerService = new CustomerService(customerRepository, entitiesConverter, payloadsConverter);

        customerEntity = DataFactory.createCustomerEntity(1L, "test name", "test password", "test@email.store", true, true);
        customerPayload = DataFactory.createCustomerPayload(1L, "test name", "test password", "test@email.store", true, true);
    }


    def "should_return_customer_when_id_is_exist_test"() {
        given:
        long id = customerEntity.getId();
        customerRepository.getById(id) >> customerEntity
        def customerPayload = entitiesConverter.convertCustomer(customerEntity)


        when:
        def customerPayloadReturnedFromService = customerService.getById(customerPayload.getId());


        then:
        assert customerPayload == customerPayloadReturnedFromService
        1 * customerRepository.getById(id) >> customerEntity
    }

//    def "should_throw_exception_when_id_is_not_exist_test"() {
//        given:
//
//
//
//        when:
//
//
//
//        then:
//
//    }
//
//    def "should_return_customers_list_when_list_is_not_empty_test"() {
//        given:
//
//
//
//        when:
//
//
//
//        then:
//
//    }
//
//    def "should_return_empty_customers_list_when_list_is_empty_test"() {
//        given:
//
//
//
//        when:
//
//
//
//        then:
//
//    }
//
//    def "should_add_customer_when_payload_is_correct_test"() {
//        given:
//
//
//
//        when:
//
//
//
//        then:
//
//    }
//
//    def "should_throw_exception_when_email_is_exist_during_save_test"() {
//        given:
//
//
//
//        when:
//
//
//
//        then:
//
//    }
//
//    def "should_edit_customer_when_payload_is_correct_test"() {
//        given:
//
//
//
//        when:
//
//
//
//        then:
//
//    }
//
//    def "should_throw_exception_when_email_and_id_not_belong_to_same_customer_test"() {
//        given:
//
//
//
//        when:
//
//
//
//        then:
//
//    }
//
//    def "should_set_is_active_field_to_false_test"() {
//        given:
//
//
//
//        when:
//
//
//
//        then:
//
//    }
//
//    def "should_throw_exception_when_id_is_not_exist_during_delete_test"() {
//        given:
//
//
//
//        when:
//
//
//
//        then:
//
//    }


}
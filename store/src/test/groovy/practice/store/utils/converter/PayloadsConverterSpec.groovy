package practice.store.utils.converter

import groovy.json.JsonOutput
import org.springframework.security.crypto.password.PasswordEncoder
import practice.store.DataFactory
import spock.lang.Specification


class PayloadsConverterSpec extends Specification {


    def "should return converted entity from payload"() {
        given:
        def passwordEncoder = Mock(PasswordEncoder.class)
        def payloadConverter = new PayloadsConverter(passwordEncoder)
        def customerPayload = DataFactory.createCustomerPayload(1L, "test name", "test password", "test@email.store", true, true)

        String password = "test password"
        String encodedPassword = "encoded test password"
        passwordEncoder.encode(password) >> encodedPassword


        when:
        def customerEntity = payloadConverter.convertCustomer(customerPayload)


        then:
        assert new JsonOutput().toJson(customerPayload).compareToIgnoreCase("password")
                ==
                new JsonOutput().toJson(customerEntity).compareToIgnoreCase("password")

        assert customerEntity.getPassword() == encodedPassword
    }
}
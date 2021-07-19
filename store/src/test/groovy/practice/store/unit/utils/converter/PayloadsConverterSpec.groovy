package practice.store.unit.utils.converter

import groovy.json.JsonOutput
import org.springframework.security.crypto.password.PasswordEncoder
import practice.DataFactoryPayloads
import practice.store.utils.converter.PayloadsConverter
import spock.lang.Specification


class PayloadsConverterSpec extends Specification {


    def "should return converted entity from payload"() {
        given:
        def passwordEncoder = Mock(PasswordEncoder.class)
        def payloadConverter = new PayloadsConverter(passwordEncoder)
        def customerPayload = DataFactoryPayloads.createCustomerPayload(1L, "test name", "test password", "test@email.store", true, true)

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
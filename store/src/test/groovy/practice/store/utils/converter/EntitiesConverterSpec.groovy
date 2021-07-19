package practice.store.utils.converter

import groovy.json.JsonOutput
import spock.lang.Specification
import testdata.DataFactoryCustomer

class EntitiesConverterSpec extends Specification {


    def "should return converted payload from entity"() {
        given:
        def entitiesConverter = new EntitiesConverter()
        def customerEntity = DataFactoryCustomer.createCustomerEntity(1L, "test name", "test@email.store", true, true)


        when:
        def customerPayload = entitiesConverter.convertCustomer(customerEntity)


        then:
        assert new JsonOutput().toJson(customerPayload)
                ==
                new JsonOutput().toJson(customerEntity)

        assert customerPayload.getPassword() == null
    }
}
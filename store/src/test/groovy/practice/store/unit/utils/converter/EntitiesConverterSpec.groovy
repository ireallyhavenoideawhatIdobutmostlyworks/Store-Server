package practice.store.unit.utils.converter

import groovy.json.JsonOutput
import practice.store.DataFactory
import practice.store.utils.converter.EntitiesConverter
import spock.lang.Specification

class EntitiesConverterSpec extends Specification {


    def "should return converted payload from entity"() {
        given:
        def entitiesConverter = new EntitiesConverter()
        def customerEntity = DataFactory.createCustomerEntity(1L, "test name", "test@email.store", true, true)


        when:
        def customerPayload = entitiesConverter.convertCustomer(customerEntity)


        then:
        assert new JsonOutput().toJson(customerPayload)
                ==
                new JsonOutput().toJson(customerEntity)

        assert customerPayload.getPassword() == null
    }
}
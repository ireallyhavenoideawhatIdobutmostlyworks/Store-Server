package practice.store.customer;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(value = "/api/customer", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Api(tags = "Customers")
@RestController
public class CustomerController {

    private final CustomerService customerService;


    @ApiOperation(value = "This method is used to get the specific customer.")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CustomerPayload> getById(@PathVariable("id") long id) {
        CustomerPayload customer = customerService.getById(id);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @ApiOperation(value = "This method is used to get the customers.")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<CustomerPayload>> getCustomersList() {
        List<CustomerPayload> customers = customerService.getList();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @ApiOperation(value = "This method is used to save the customer.")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity save(@Valid @RequestBody CustomerPayload customer) {
        customerService.save(customer);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "This method is used to edit specific customer.")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity edit(@Valid @RequestBody CustomerPayload customer, @PathVariable("id") long id) {
        customerService.edit(customer, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "This method is used to set false in 'isActive' field belong to specific customer.")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity remove(@PathVariable("id") long id) {
        customerService.setIsActiveToFalse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

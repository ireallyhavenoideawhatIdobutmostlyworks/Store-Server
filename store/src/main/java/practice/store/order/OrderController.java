package practice.store.order;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping(value = "/api/order", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Api(tags = "Orders")
@RestController
public class OrderController {

    private final OrderService orderService;


    @ApiOperation(value = "This method is used to save the order.")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity save(@Valid @RequestBody OrderPayload orderPayload) {
        orderService.save(orderPayload);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiImplicitParam(name = "id", value = "Customer ID", required = true, dataType = "Long", paramType = "path")
    @ApiOperation(value = "This method is used to get the specific client.")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OrderPayload> getById(@PathVariable("id") long id) {
        OrderPayload order = orderService.getById(id);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}

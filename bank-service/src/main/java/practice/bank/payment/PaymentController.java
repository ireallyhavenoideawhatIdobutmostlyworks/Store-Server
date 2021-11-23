package practice.bank.payment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping(value = "/api/payment", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Api(tags = "Payments")
@RestController
public class PaymentController {

    private final PaymentService paymentService;


    @ApiOperation(value = "This method is used to launch payment logic.")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity save(@Valid @RequestBody PaymentResultPayload paymentResultPayload) {
        boolean isSuccess = paymentService.processingPayment(paymentResultPayload);
        if (isSuccess) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else
            return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
    }
}

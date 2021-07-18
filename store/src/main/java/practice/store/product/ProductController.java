package practice.store.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(value = "/api/product", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Api( tags = "Products")
@RestController
public class ProductController {

    private final ProductService productService;


    @ApiOperation(value = "This method is used to get the specific product.")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ProductPayload> getById(@PathVariable("id") long id) {
        ProductPayload product = productService.getById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @ApiOperation(value = "This method is used to get the products.")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<ProductPayload>> getProductsList() {
        List<ProductPayload> products = productService.getList();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}

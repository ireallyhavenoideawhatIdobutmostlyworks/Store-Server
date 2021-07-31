package practice.store.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import practice.store.customer.CustomerEntity;
import practice.store.exceptions.customer.CustomerEmailExistException;
import practice.store.exceptions.product.*;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.numbers.CalculatePriceProduct;
import practice.store.utils.values.GenerateRandomString;
import testdata.DataFactoryProduct;
import practice.store.utils.converter.EntitiesConverter;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for product service")
class ProductServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProductRepository productRepository;
    private ProductService productService;

    private EntitiesConverter entitiesConverter;
    private PayloadsConverter payloadsConverter;

    private ProductEntity productEntity;
    private ProductEntity productEntityWithDiscount;
    private ProductEntity productEntityWithoutDiscount;


    private ProductPayload productPayloadWithoutDiscount;

    private GenerateRandomString generateRandomString;
    private CalculatePriceProduct calculateFinalPrice;


    @BeforeEach
    void setUp() {
        entitiesConverter = new EntitiesConverter();
        payloadsConverter = new PayloadsConverter(passwordEncoder);

        generateRandomString = new GenerateRandomString();
        calculateFinalPrice = new CalculatePriceProduct();

        productService = new ProductService(productRepository, entitiesConverter, payloadsConverter, generateRandomString, calculateFinalPrice);

        productEntity = DataFactoryProduct.createProductEntity(1L, "test name", "testUUID", "test description", 100, 10, 90, 10, true, 5, Categories.PHONES, Availability.AVAILABLE, true);
        productEntityWithDiscount = DataFactoryProduct.createProductEntity(1L, "test name", "testUUID", "test description", 100, 10, 90, 10, true, 5, Categories.PHONES, Availability.AVAILABLE, true);
        productEntityWithoutDiscount = DataFactoryProduct.createProductEntity(1L, "test name", "testUUID", "test description", 100, 0, 100, 0, false, 5, Categories.PHONES, Availability.AVAILABLE, true);

        productPayloadWithoutDiscount = DataFactoryProduct.createProductPayload(1L, "test name", "testUUID", "test description", 100, 0, 100, 0, false, 5, Categories.PHONES, Availability.AVAILABLE, true);
    }


    @DisplayName("Return product by ID")
    @Test
    void should_return_product_when_id_is_exist_test() {
        // given
        long id = productEntity.getId();

        when(productRepository.getById(id)).thenReturn(productEntity);
        ProductPayload productPayload = entitiesConverter.convertProduct(productEntity);


        // when
        ProductPayload productPayloadReturnedFromService = productService.getById(id);


        // then
        assertEquals(
                productPayload,
                productPayloadReturnedFromService);

        verify(productRepository, times(1)).getById(id);
    }

    @DisplayName("Throw exception when ID is not exist")
    @Test
    void should_throw_exception_when_id_is_not_exist_test() {
        // given
        String exceptionMessage = "Unable to find practice.store.product.ProductEntity with id %d";
        long idNotExist = 11L;

        when(productRepository.getById(idNotExist))
                .thenThrow(new EntityNotFoundException(String.format(exceptionMessage, idNotExist)));


        // when
        Throwable exception = catchThrowable(() -> productService.getById(idNotExist));


        // then
        assertThat(exception)
                .isInstanceOf(javax.persistence.EntityNotFoundException.class)
                .hasMessageContaining(String.format(exceptionMessage, idNotExist));

        verify(productRepository, times(1)).getById(idNotExist);
    }

    @DisplayName("Return not empty products list")
    @Test
    void should_return_products_list_when_list_is_not_empty_test() {
        // given
        List<ProductEntity> productEntityList = Collections.singletonList(productEntity);
        when(productRepository.findAll()).thenReturn(productEntityList);
        List<ProductPayload> productPayloadList = Collections.singletonList(entitiesConverter.convertProduct(productEntity));


        // when
        List<ProductPayload> productPayloadListReturnedFromService = productService.getProducts();


        // then
        assertThat(productPayloadList)
                .usingRecursiveComparison()
                .isEqualTo(productPayloadListReturnedFromService);

        verify(productRepository, times(1)).findAll();
    }

    @DisplayName("Return empty products list")
    @Test
    void should_return_empty_products_list_when_list_is_empty_test() {
        // given
        List<ProductEntity> emptyProductEntityList = new ArrayList<>();
        when(productRepository.findAll()).thenReturn(emptyProductEntityList);


        // when
        List<ProductPayload> productPayloadListReturnedFromService = productService.getProducts();


        // then
        assertTrue(productPayloadListReturnedFromService.isEmpty());

        verify(productRepository, times(1)).findAll();
    }

    @DisplayName("Save product with discount")
    @Test
    void should_save_product_with_discount() {
        // given
        ProductPayload productPayloadWithDiscount = DataFactoryProduct.createProductPayload(1L, "test name", "testUUID", "test description", 100, 10, 90, 10, true, 5, Categories.PHONES, Availability.AVAILABLE, true);

        productPayloadWithDiscount.setId(null);
        productPayloadWithDiscount.setActive(true);

        ProductEntity productEntity = payloadsConverter.convertProduct(productPayloadWithDiscount);


        // when
        productService.save(productPayloadWithDiscount);


        // then
        verify(productRepository, times(1)).save(productEntity);
    }

    @DisplayName("Save product without discount")
    @Test
    void should_save_product_without_discount() {
        // given
        ProductPayload productPayloadWithDiscount = DataFactoryProduct.createProductPayload(1L, "test name", "testUUID", "test description", 100, 0, 100, 0, false, 5, Categories.PHONES, Availability.AVAILABLE, true);

        productPayloadWithDiscount.setId(null);
        productPayloadWithDiscount.setActive(true);

        ProductEntity productEntity = payloadsConverter.convertProduct(productPayloadWithDiscount);


        // when
        productService.save(productPayloadWithDiscount);


        // then
        verify(productRepository, times(1)).save(productEntity);
    }

    @DisplayName("Throw exception when product is withdraw from sale during save")
    @Test
    void should_throw_exception_when_product_is_withdraw_from_sale_during_save_test() {
        // given
        ProductPayload productPayloadWithDiscount = DataFactoryProduct.createProductPayload(1L, "test name", "testUUID", "test description", 100, 0, 100, 0, false, 5, Categories.PHONES, Availability.WITHDRAW_FROM_SALE, true);
        String exceptionMessage = "A product withdrawn from sale cannot be added.";


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductWithdrawFromSaleException.class)
                .hasMessageContaining(exceptionMessage);

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception when product uuid is exist during save")
    @Test
    void should_throw_exception_when_product_uuid_is_exist_during_save_test() {
        // given
        String uuid = "test uuid";
        ProductPayload productPayloadWithDiscount = DataFactoryProduct.createProductPayload(1L, "test name", uuid, "test description", 100, 0, 100, 0, false, 5, Categories.PHONES, Availability.AVAILABLE, true);
        String exceptionMessage = "Product with UUID:%s is exist.";

        when(productRepository.existsByProductUUID(uuid)).thenReturn(true);


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductUuidExistException.class)
                .hasMessageContaining(String.format(exceptionMessage, uuid));

        verify(productRepository, times(1)).existsByProductUUID(uuid);
        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product with discount when discount percentage is to high during save")
    @Test
    void should_throw_exception_when_product_discount_percentage_is_to_high_during_save_test() {
        // given
        ProductPayload productPayloadWithDiscount = DataFactoryProduct.createProductPayload(1L, "test name", "testUUID", "test description", 100, 96, 4, 96, true, 5, Categories.PHONES, Availability.AVAILABLE, true);
        String exceptionMessage = "The percentage discount may not be higher than 95%.";


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductDiscountPercentageHighException.class)
                .hasMessageContaining(exceptionMessage);

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product with discount when discount percentage is to low during save")
    @Test
    void should_throw_exception_when_product_discount_percentage_is_to_low_during_save_test() {
        // given
        ProductPayload productPayloadWithDiscount = DataFactoryProduct.createProductPayload(1L, "test name", "testUUID", "test description", 100, 4, 96, 4, true, 5, Categories.PHONES, Availability.AVAILABLE, true);
        String exceptionMessage = "The percentage discount may not be lower than 5%.";


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductDiscountPercentageLowException.class)
                .hasMessageContaining(exceptionMessage);

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product with discount when price after discount calculate is incorrect during save")
    @Test
    void should_throw_exception_when_product_final_price_is_incorrect_during_save_test() {
        // given
        double basePrice = 100;
        int discountPercentage = 10;
        double incorrectFinalPrice = 88;
        ProductPayload productPayloadWithDiscount = DataFactoryProduct.createProductPayload(1L, "test name", "testUUID", "test description", basePrice, 10, incorrectFinalPrice, discountPercentage, true, 5, Categories.PHONES, Availability.AVAILABLE, true);

        String exceptionMessage = "Incorrect final price. Final price from payload:%f. Correct final price:%f.";
        double finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductFinalPriceException.class)
                .hasMessageContaining(String.format(exceptionMessage, incorrectFinalPrice, finalPriceCalculate));

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product with discount when price reduction is incorrect during save")
    @Test
    void should_throw_exception_when_product_price_reduction_is_incorrect_during_save_test() {
        // given
        double basePrice = 100;
        int discountPercentage = 10;
        double amountPriceReduction = 11;
        ProductPayload productPayloadWithDiscount = DataFactoryProduct.createProductPayload(1L, "test name", "testUUID", "test description", basePrice, amountPriceReduction, 90, discountPercentage, true, 5, Categories.PHONES, Availability.AVAILABLE, true);

        String exceptionMessage = "Incorrect price reduction. Price reduction from payload:%f. Correct price reduction:%f.";
        double finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);
        double amountPriceReductionCalculate = basePrice - finalPriceCalculate;


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductPriceReductionException.class)
                .hasMessageContaining(String.format(exceptionMessage, amountPriceReduction, amountPriceReductionCalculate));

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product without discount when discount percentage is not zero during save")
    @Test
    void should_throw_exception_when_discount_percentage_is_not_zero_during_save_test() {
        // given



        // when


        // then

    }

    @DisplayName("Throw exception for product without discount when price reduction is not zero during save")
    @Test
    void should_throw_exception_when_discount_price_reduction_is_not_zero_during_save_test() {
        // given



        // when


        // then

    }

    @DisplayName("Throw exception for product without discount when final price and base price are not equal during save")
    @Test
    void should_throw_exception_when_prices_are_not_equal_during_save_test() {
        // given



        // when


        // then

    }


}
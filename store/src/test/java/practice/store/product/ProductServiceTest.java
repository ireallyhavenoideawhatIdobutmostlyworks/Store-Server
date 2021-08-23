package practice.store.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import practice.store.exceptions.product.*;
import practice.store.utils.converter.EntitiesConverter;
import practice.store.utils.converter.PayloadsConverter;
import practice.store.utils.numbers.CalculatePriceProduct;
import practice.store.utils.values.GenerateRandomString;
import testdata.entity.TestDataProductEntity;
import testdata.payload.TestDataProductPayload;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestPropertySource("classpath:productsDiscountValueTest.properties")
@DisplayName("Tests for product service")
@ExtendWith(SpringExtension.class)
@ContextConfiguration
class ProductServiceTest {

    @Value("${discount.percentage.max.higher.value}")
    private int discountPercentageMaxHigherValue;
    @Value("${discount.percentage.max.lower.value}")
    private int discountPercentageMaxLowerValue;

    @Mock
    private ProductRepository productRepository;
    private ProductService productService;

    private EntitiesConverter entitiesConverter;
    private PayloadsConverter payloadsConverter;

    private ProductEntity productEntity;
    private List<ProductEntity> productEntityList;
    private ProductPayload productPayloadWithDiscount;
    private ProductPayload productPayloadWithoutDiscount;

    private CalculatePriceProduct calculateFinalPrice;


    @BeforeEach
    void setUp() {
        entitiesConverter = new EntitiesConverter();
        payloadsConverter = new PayloadsConverter(mock(PasswordEncoder.class));

        calculateFinalPrice = new CalculatePriceProduct();

        productService = new ProductService(productRepository, entitiesConverter, payloadsConverter, new GenerateRandomString(), calculateFinalPrice, discountPercentageMaxHigherValue, discountPercentageMaxLowerValue);

        productEntity = TestDataProductEntity.Product();
        productEntityList = TestDataProductEntity.ProductList();

        productPayloadWithDiscount = TestDataProductPayload.ProductWithDiscount();
        productPayloadWithoutDiscount = TestDataProductPayload.ProductWithoutDiscount();
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
        productPayloadWithoutDiscount.setId(null);
        productPayloadWithoutDiscount.setActive(true);

        ProductEntity productEntity = payloadsConverter.convertProduct(productPayloadWithoutDiscount);


        // when
        productService.save(productPayloadWithoutDiscount);


        // then
        verify(productRepository, times(1)).save(productEntity);
    }

    @DisplayName("Throw exception when product is withdraw from sale during save")
    @Test
    void should_throw_exception_when_product_is_withdraw_from_sale_during_save_test() {
        // given
        productPayloadWithDiscount.setAvailability(Availability.WITHDRAW_FROM_SALE);


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductWithdrawFromSaleException.class)
                .hasMessage(String.format("A product withdrawn from sale cannot be added. Name: %s, UUID: %s", productPayloadWithDiscount.getName(), productPayloadWithDiscount.getProductUUID()));

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception when product uuid is exist during save")
    @Test
    void should_throw_exception_when_product_uuid_is_exist_during_save_test() {
        // given
        String uuid = productPayloadWithDiscount.getProductUUID();
        when(productRepository.existsByProductUUID(uuid)).thenReturn(true);


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductUuidExistException.class)
                .hasMessage(String.format("Product with UUID:%s is exist.", uuid));

        verify(productRepository, times(1)).existsByProductUUID(uuid);
        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product with discount when discount percentage is to high during save")
    @Test
    void should_throw_exception_when_product_discount_percentage_is_to_high_during_save_test() {
        // given
        productPayloadWithDiscount.setDiscountPercentage(96);


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductDiscountPercentageHighException.class)
                .hasMessage(String.format("The percentage discount may not be higher than %d%%.", discountPercentageMaxHigherValue));

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product with discount when discount percentage is to low during save")
    @Test
    void should_throw_exception_when_product_discount_percentage_is_to_low_during_save_test() {
        // given
        productPayloadWithDiscount.setDiscountPercentage(4);


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductDiscountPercentageLowException.class)
                .hasMessage(String.format("The percentage discount may not be lower than %d%%.", discountPercentageMaxLowerValue));

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product with discount when price after discount calculate is incorrect during save")
    @Test
    void should_throw_exception_when_product_final_price_is_incorrect_during_save_test() {
        // given
        BigDecimal basePrice = BigDecimal.valueOf(100);
        int discountPercentage = 10;
        BigDecimal incorrectFinalPrice = BigDecimal.valueOf(88);

        productPayloadWithDiscount.setBasePrice(basePrice);
        productPayloadWithDiscount.setDiscountPercentage(discountPercentage);
        productPayloadWithDiscount.setFinalPrice(incorrectFinalPrice);

        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductFinalPriceException.class)
                .hasMessage(String.format("Incorrect final price. Final price from payload:%.2f. Correct final price:%.2f.", incorrectFinalPrice, finalPriceCalculate));

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product with discount when price reduction is incorrect during save")
    @Test
    void should_throw_exception_when_product_price_reduction_is_incorrect_during_save_test() {
        // given
        BigDecimal basePrice = BigDecimal.valueOf(100);
        int discountPercentage = 10;
        BigDecimal amountPriceReduction = BigDecimal.valueOf(11);

        productPayloadWithDiscount.setBasePrice(basePrice);
        productPayloadWithDiscount.setDiscountPercentage(discountPercentage);
        productPayloadWithDiscount.setAmountPriceReduction(amountPriceReduction);

        BigDecimal finalPriceCalculate = calculateFinalPrice.calculateFinalPrice(basePrice, discountPercentage);
        BigDecimal amountPriceReductionCalculate = basePrice.subtract(finalPriceCalculate);


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductPriceReductionException.class)
                .hasMessage(String.format("Incorrect price reduction. Price reduction from payload:%.2f. Correct price reduction:%.2f.", amountPriceReduction, amountPriceReductionCalculate));

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product without discount when discount percentage is not zero during save")
    @Test
    void should_throw_exception_when_discount_percentage_is_not_zero_during_save_test() {
        // given
        productPayloadWithoutDiscount.setDiscountPercentage(10);


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithoutDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductDiscountPercentageException.class)
                .hasMessage("Discount percentage should be equal 0 because that product is without discount.");

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product without discount when price reduction is not zero during save")
    @Test
    void should_throw_exception_when_discount_price_reduction_is_not_zero_during_save_test() {
        // given
        productPayloadWithoutDiscount.setAmountPriceReduction(BigDecimal.valueOf(10));


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithoutDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductPriceReductionException.class)
                .hasMessage("Price reduction should equal 0 because that product is without discount.");

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for product without discount when final price and base price are not equal during save")
    @Test
    void should_throw_exception_when_prices_are_not_equal_during_save_test() {
        // given
        productPayloadWithoutDiscount.setBasePrice(BigDecimal.valueOf(100));
        productPayloadWithoutDiscount.setFinalPrice(BigDecimal.valueOf(90));


        // when
        Throwable exception = catchThrowable(() -> productService.save(productPayloadWithoutDiscount));


        // then
        assertThat(exception)
                .isInstanceOf(ProductFinalAndBasePriceException.class)
                .hasMessage("Final price should equal base price because that product is without discount.");

        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Throw exception for entity not found during edit")
    @Test
    void should_throw_exception_when_product_not_exist_during_edit_test() {
        // given
        String uuidNotExist = "Incorrect UUID";
        when(productRepository.existsByProductUUID(uuidNotExist)).thenThrow(new EntityNotFoundException());


        // when
        Throwable exception = catchThrowable(() -> productService.edit(productPayloadWithoutDiscount, uuidNotExist));


        // then
        assertThat(exception)
                .isInstanceOf(javax.persistence.EntityNotFoundException.class);

        verify(productRepository, times(1)).existsByProductUUID(uuidNotExist);
        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Edit product with discount")
    @Test
    void should_edit_product_with_discount_during_edit_test() {
        // given
        String uuid = productPayloadWithDiscount.getProductUUID();

        productPayloadWithDiscount.setBasePrice(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP));
        productPayloadWithDiscount.setDiscountPercentage(50);
        productPayloadWithDiscount.setFinalPrice(BigDecimal.valueOf(500).setScale(2, RoundingMode.HALF_UP));
        productPayloadWithDiscount.setAmountPriceReduction(BigDecimal.valueOf(500).setScale(2, RoundingMode.HALF_UP));

        when(productRepository.existsByProductUUID(uuid)).thenReturn(true);
        when(productRepository.findByProductUUID(uuid)).thenReturn(productEntity);
        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayloadWithDiscount);


        // when
        productService.edit(productPayloadWithDiscount, uuid);


        // then
        verify(productRepository, times(1)).existsByProductUUID(uuid);
        verify(productRepository, times(1)).findByProductUUID(uuid);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @DisplayName("Edit product without discount")
    @Test
    void should_edit_product_without_discount_during_edit_test() {
        // given
        String uuid = productPayloadWithDiscount.getProductUUID();

        productPayloadWithDiscount.setHasDiscount(false);
        productPayloadWithDiscount.setFinalPrice(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));
        productPayloadWithDiscount.setBasePrice(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));
        productPayloadWithDiscount.setDiscountPercentage(0);
        productPayloadWithDiscount.setAmountPriceReduction(BigDecimal.valueOf(0));

        when(productRepository.existsByProductUUID(uuid)).thenReturn(true);
        when(productRepository.findByProductUUID(uuid)).thenReturn(productEntity);
        ProductEntity existingProduct = payloadsConverter.convertProduct(productPayloadWithDiscount);


        // when
        productService.edit(productPayloadWithDiscount, uuid);


        // then
        verify(productRepository, times(1)).existsByProductUUID(uuid);
        verify(productRepository, times(1)).findByProductUUID(uuid);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @DisplayName("Remove product if never was bought")
    @Test
    void should_remove_product_if_never_was_bought_during_remove_test() {
        // given
        String uuid = productEntity.getProductUUID();
        when(productRepository.existsByProductUUID(uuid)).thenReturn(true);
        when(productRepository.findByProductUUID(uuid)).thenReturn(productEntity);
        when(productRepository.whetherTheProductWasBought(productEntity.getId())).thenReturn(false);


        // when
        productService.setWithdrawFromSale(uuid);


        // then
        verify(productRepository, times(1)).existsByProductUUID(uuid);
        verify(productRepository, times(1)).findByProductUUID(uuid);
        verify(productRepository, times(1)).whetherTheProductWasBought(productEntity.getId());
        verify(productRepository, times(1)).delete(productEntity);
        verify(productRepository, times(0)).save(productEntity);
    }

    @DisplayName("Set WITHDRAW_FROM_SALE if product was bought")
    @Test
    void should_set_withdraw_from_sale_if_product_was_bought_during_remove_test() {
        // given
        String uuid = productEntity.getProductUUID();
        when(productRepository.existsByProductUUID(uuid)).thenReturn(true);
        when(productRepository.findByProductUUID(uuid)).thenReturn(productEntity);
        when(productRepository.whetherTheProductWasBought(productEntity.getId())).thenReturn(true);


        // when
        productService.setWithdrawFromSale(uuid);


        // then
        assertEquals(Availability.WITHDRAW_FROM_SALE, productEntity.getAvailability());
        assertFalse(productEntity.isActive());

        verify(productRepository, times(1)).existsByProductUUID(uuid);
        verify(productRepository, times(1)).findByProductUUID(uuid);
        verify(productRepository, times(1)).whetherTheProductWasBought(productEntity.getId());
        verify(productRepository, times(1)).save(productEntity);
        verify(productRepository, times(0)).delete(productEntity);
    }
}
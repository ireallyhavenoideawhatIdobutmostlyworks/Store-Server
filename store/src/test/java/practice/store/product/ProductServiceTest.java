package practice.store.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private ProductRepository productRepository;
    private ProductService productService;

    private EntitiesConverter entitiesConverter;

    private ProductEntity productEntity;


    @BeforeEach
    void setUp() {
        entitiesConverter = new EntitiesConverter();

        productService = new ProductService(productRepository, entitiesConverter);

        productEntity = DataFactoryProduct.createProductEntity(1L, "test name", "testUUID", "test description", 100, 10, 90, 10, true, 5, Categories.PHONES, Availability.AVAILABLE, true);
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
        List<ProductPayload> productPayloadListReturnedFromService = productService.getList();


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
        List<ProductPayload> productPayloadListReturnedFromService = productService.getList();


        // then
        assertTrue(productPayloadListReturnedFromService.isEmpty());

        verify(productRepository, times(1)).findAll();
    }
}
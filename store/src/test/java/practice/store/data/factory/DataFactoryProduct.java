package practice.store.data.factory;

import practice.store.product.Availability;
import practice.store.product.Categories;
import practice.store.product.ProductEntity;
import practice.store.product.ProductPayload;

import java.util.Arrays;
import java.util.List;

public class DataFactoryProduct {

    public static ProductEntity createProductEntity(long id, String name, String productUUID, String description, double basePrice, double amountPriceReduction, double finalPrice, int discountPercentage, boolean hasDiscount, int amountInStock, Categories categories, Availability availability, boolean isActive) {
        return ProductEntity.builder()
                .id(id)
                .name(name)
                .productUUID(productUUID)
                .description(description)
                .basePrice(basePrice)
                .amountPriceReduction(amountPriceReduction)
                .finalPrice(finalPrice)
                .discountPercentage(discountPercentage)
                .hasDiscount(hasDiscount)
                .amountInStock(amountInStock)
                .categories(categories)
                .availability(availability)
                .isActive(isActive)
                .build();
    }

    public static ProductPayload createProductPayload(long id, String name, String productUUID, String description, double basePrice, double amountPriceReduction, double finalPrice, int discountPercentage, boolean hasDiscount, int amountInStock, Categories categories, Availability availability, boolean isActive) {
        return ProductPayload.builder()
                .id(id)
                .name(name)
                .productUUID(productUUID)
                .description(description)
                .basePrice(basePrice)
                .amountPriceReduction(amountPriceReduction)
                .finalPrice(finalPrice)
                .discountPercentage(discountPercentage)
                .hasDiscount(hasDiscount)
                .amountInStock(amountInStock)
                .categories(categories)
                .availability(availability)
                .isActive(isActive)
                .build();
    }

    public static ProductEntity createProductEntity() {
        return ProductEntity.builder()
                .id(1L)
                .name("test name")
                .productUUID("testUUID")
                .description("test description")
                .basePrice(100)
                .amountPriceReduction(10)
                .finalPrice(90)
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static List<ProductEntity> creteProductList() {
        ProductEntity existingProductEntityFirst = ProductEntity
                .builder()
                .id(1L)
                .name("test name 1")
                .productUUID("testUUID 1")
                .description("test description 1")
                .basePrice(100)
                .amountPriceReduction(10)
                .finalPrice(90)
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();

        ProductEntity existingProductEntitySecond = ProductEntity
                .builder()
                .id(2L)
                .name("test name 2")
                .productUUID("testUUID 2")
                .description("test description 2")
                .basePrice(100)
                .amountPriceReduction(10)
                .finalPrice(90)
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();


        ProductEntity existingProductEntityThird = ProductEntity
                .builder()
                .id(3L)
                .name("test name 3")
                .productUUID("testUUID 3")
                .description("test description 3")
                .basePrice(100)
                .amountPriceReduction(10)
                .finalPrice(90)
                .discountPercentage(10)
                .hasDiscount(true)
                .amountInStock(5)
                .categories(Categories.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();

        return Arrays.asList(existingProductEntityFirst, existingProductEntitySecond, existingProductEntityThird);
    }
}


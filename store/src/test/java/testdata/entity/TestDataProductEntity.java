package testdata.entity;

import practice.store.product.Availability;
import practice.store.product.CategoriesEnum;
import practice.store.product.ProductEntity;
import practice.store.utils.values.GenerateRandomString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public abstract class TestDataProductEntity {

    public static ProductEntity Product() {
        return ProductEntity.builder()
                .id(1L)
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(5)
                .categoriesEnum(CategoriesEnum.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductEntity Product(String uuid) {
        return ProductEntity.builder()
                .id(1L)
                .name("test name")
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(5)
                .categoriesEnum(CategoriesEnum.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductEntity Product(long id, String name, String productUUID, String description, BigDecimal basePrice, BigDecimal amountPriceReduction, BigDecimal finalPrice, int discountPercentage, boolean hasDiscount, int amountInStock, CategoriesEnum categoriesEnum, Availability availability, boolean isActive) {
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
                .amount(amountInStock)
                .categoriesEnum(categoriesEnum)
                .availability(availability)
                .isActive(isActive)
                .build();
    }

    public static ProductEntity ProductWithoutDiscount() {
        return ProductEntity.builder()
                .id(1L)
                .name("test name")
                .productUUID(new GenerateRandomString().generateRandomUuid())
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(0))
                .finalPrice(setBigDecimalWithScale(100))
                .discountPercentage(0)
                .hasDiscount(false)
                .amount(5)
                .categoriesEnum(CategoriesEnum.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static ProductEntity ProductWithoutDiscount(String uuid) {
        return ProductEntity.builder()
                .id(1L)
                .name("test name")
                .productUUID(uuid)
                .description("test description")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(0))
                .finalPrice(setBigDecimalWithScale(100))
                .discountPercentage(0)
                .hasDiscount(false)
                .amount(5)
                .categoriesEnum(CategoriesEnum.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();
    }

    public static List<ProductEntity> ProductList() {
        ProductEntity existingProductEntityFirst = ProductEntity
                .builder()
                .id(11L)
                .name("test name 1")
                .productUUID("testUUID 1")
                .description("test description 1")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(5)
                .categoriesEnum(CategoriesEnum.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();

        ProductEntity existingProductEntitySecond = ProductEntity
                .builder()
                .id(12L)
                .name("test name 2")
                .productUUID("testUUID 2")
                .description("test description 2")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(5)
                .categoriesEnum(CategoriesEnum.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();

        ProductEntity existingProductEntityThird = ProductEntity
                .builder()
                .id(13L)
                .name("test name 3")
                .productUUID("testUUID 3")
                .description("test description 3")
                .basePrice(setBigDecimalWithScale(100))
                .amountPriceReduction(setBigDecimalWithScale(10))
                .finalPrice(setBigDecimalWithScale(90))
                .discountPercentage(10)
                .hasDiscount(true)
                .amount(5)
                .categoriesEnum(CategoriesEnum.PHONES)
                .availability(Availability.AVAILABLE)
                .isActive(true)
                .build();

        return Arrays.asList(existingProductEntityFirst, existingProductEntitySecond, existingProductEntityThird);
    }

    protected static BigDecimal setBigDecimalWithScale(long val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.HALF_UP);
    }
}

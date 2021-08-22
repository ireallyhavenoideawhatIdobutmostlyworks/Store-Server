package practice.store.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByProductUUID(String uuid);
    ProductEntity findByProductUUID(String uuid);

    @Query(value = "SELECT CAST" +
            "(CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS BIT) " +
            "FROM ORDERS_PRODUCTS " +
            "WHERE PRODUCT_ID = ?1", nativeQuery = true)
    boolean whetherTheProductWasBought(long id);
}

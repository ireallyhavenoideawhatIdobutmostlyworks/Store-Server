package practice.storage.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByProductUUID(String uuid);
    ProductEntity findByProductUUID(String uuid);
}

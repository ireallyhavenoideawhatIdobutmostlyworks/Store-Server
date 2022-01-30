package practice.store.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByProductUUID(String uuid);
    Optional<ProductEntity> findByProductUUID(String uuid);
}

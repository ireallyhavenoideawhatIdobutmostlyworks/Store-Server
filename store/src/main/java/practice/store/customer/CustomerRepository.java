package practice.store.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndId(String email, long id);

    CustomerEntity findByEmail(String email);
}

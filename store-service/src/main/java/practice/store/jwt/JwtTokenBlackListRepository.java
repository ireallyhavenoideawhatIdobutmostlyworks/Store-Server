package practice.store.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenBlackListRepository extends JpaRepository<JwtTokenBlackListEntity, Long> {

    boolean existsByToken(String token);
}

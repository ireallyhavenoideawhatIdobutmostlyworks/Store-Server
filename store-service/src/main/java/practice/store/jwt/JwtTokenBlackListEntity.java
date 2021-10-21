package practice.store.jwt;

import lombok.Builder;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Builder
@Entity
@Table(name = "tokens_black_list")
public class JwtTokenBlackListEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private final Long id;

    @Column
    private final String token;
}

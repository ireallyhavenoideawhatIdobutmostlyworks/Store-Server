package practice.store.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import practice.store.customer.CustomerEntity;
import practice.store.customer.CustomerRepository;
import practice.store.exceptions.customer.CustomerIsNotActiveException;
import practice.store.jwt.payload.JwtLoginResponsePayload;
import practice.store.jwt.payload.JwtLogoutResponsePayload;
import practice.store.utils.values.GenerateRandomString;

import java.time.LocalDateTime;
import java.util.ArrayList;

@PropertySource("classpath:jwt.properties")
@RequiredArgsConstructor
@Service
public class JwtService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    private final GenerateRandomString generateRandomString;

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtTokenBlackListRepository jwtTokenBlackListRepository;


    @Value("${jwt.token.validity.plus.n.minutes}")
    private long jwtTokenValidityToMinutesN;
    @Value("${jwt.secret}")
    private String secret;

    private String token;


    @Override
    public UserDetails loadUserByUsername(String email) {
        CustomerEntity customerEntity = customerRepository.findByEmail(email);

        checkIfCustomerIsActive(customerEntity);

        return new User(
                customerEntity.getEmail(),
                customerEntity.getPassword(),
                new ArrayList<>()
        );
    }

    public JwtLoginResponsePayload buildResponseLogin(UserDetails userDetails) {
        return JwtLoginResponsePayload.builder()
                .jwtToken(generateToken(userDetails))
                .minutesTokenValid(jwtTokenValidityToMinutesN)
                .timestamp(LocalDateTime.now())
                .responseID(generateRandomString.generateRandomUuid())
                .build();
    }

    public JwtLogoutResponsePayload buildResponseLogout() {
        return JwtLogoutResponsePayload.builder()
                .description("Logout success.")
                .timestamp(LocalDateTime.now())
                .responseID(generateRandomString.generateRandomUuid())
                .build();
    }

    public void addTokenToBlackListRepository() {
        JwtTokenBlackListEntity jwtTokenBlackListEntity = buildTokenEntityForBlackList(token);

        jwtTokenBlackListRepository.save(jwtTokenBlackListEntity);
    }


    private String generateToken(UserDetails userDetails) {
        token = jwtTokenUtil.generateToken(userDetails);
        return token;
    }

    private void checkIfCustomerIsActive(CustomerEntity customerEntity) {
        if (!customerEntity.isActive())
            throw new CustomerIsNotActiveException(customerEntity.getEmail());
    }

    private JwtTokenBlackListEntity buildTokenEntityForBlackList(String token) {
        return JwtTokenBlackListEntity.builder()
                .token(token)
                .build();
    }
}

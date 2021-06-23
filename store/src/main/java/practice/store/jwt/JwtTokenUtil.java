package practice.store.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@PropertySource("classpath:jwt.properties")
@RequiredArgsConstructor
@Component
public class JwtTokenUtil {

    @Value("${jwt.token.validity.plus.n.minutes}")
    private long jwtTokenValidityToMinutesN;
    @Value("${jwt.secret}")
    private String secret;


    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    public Date getIssuedAtDateFromToken(String token) {
        // to future maybe? hmm
        return getClaimFromToken(token, Claims::getIssuedAt);
    }
    public Boolean canTokenBeRefreshed(String token) {
        // to future maybe? hmm
        return (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }
    private Boolean ignoreTokenExpiration(String token) {
        // to future maybe? hmm
        return false;
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(generateTokenCreatedData())
                .setExpiration(generateTokenValidity())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    private Date generateTokenCreatedData() {
        Instant instant = LocalDateTime
                .now()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        return Date.from(instant);
    }

    private Date generateTokenValidity() {
        Instant instant = LocalDateTime
                .now()
                .plusMinutes(jwtTokenValidityToMinutesN)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        return Date.from(instant);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}

package practice.store.jwt.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class JwtLoginRequestPayload {

    private final String email;
    private final String password;
}

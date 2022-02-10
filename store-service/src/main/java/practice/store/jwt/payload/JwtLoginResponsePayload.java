package practice.store.jwt.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
@Builder
@Getter
public final class JwtLoginResponsePayload {

    private final String jwtToken;
    private final Long minutesTokenValid;
    private final LocalDateTime timestamp;
    private final String responseID;
}

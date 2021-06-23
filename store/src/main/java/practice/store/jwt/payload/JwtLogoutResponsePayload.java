package practice.store.jwt.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class JwtLogoutResponsePayload {

    private final String description;
    private final LocalDateTime timestamp;
    private final String responseID;
}

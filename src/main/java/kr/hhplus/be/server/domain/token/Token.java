package kr.hhplus.be.server.domain.token;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    private static final Duration TOKEN_EXPIRY = Duration.ofMinutes(30);

    private Long id;
    private String tokenId; // UUID
    private Long userRefId; // User ID 참조
    private String tokenValue; // JWT
    private Instant expireDate;

    public static Token create(Long userRefId, String tokenValue) {
        return Token.builder()
                .tokenId(UUID.randomUUID().toString())
                .userRefId(userRefId)
                .tokenValue(tokenValue)
                .expireDate(Instant.now().plus(TOKEN_EXPIRY))
                .build();
    }

    public boolean isValid() {
        return tokenValue != null && expireDate.isAfter(Instant.now());
    }
}

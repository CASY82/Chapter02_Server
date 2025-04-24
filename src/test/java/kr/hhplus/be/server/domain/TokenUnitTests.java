package kr.hhplus.be.server.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.token.Token;

public class TokenUnitTests {

    private Token token;
    private Long userId;
    private String tokenValue;

    @BeforeEach
    void setUp() {
        userId = 1L;
        tokenValue = "test-token-value";
        token = Token.create(userId, tokenValue);
    }

    @Test
    @DisplayName("토큰 생성 시 올바른 속성으로 초기화된다")
    void 토큰_생성_성공() {
        // given
        Long newUserId = 2L;
        String newTokenValue = "new-token-value";

        // when
        Token newToken = Token.create(newUserId, newTokenValue);

        // then
        assertThat(newToken.getTokenId()).isNotBlank();
        assertThat(UUID.fromString(newToken.getTokenId())).isNotNull(); // UUID 형식 검증
        assertThat(newToken.getUser().getId()).isEqualTo(newUserId);
        assertThat(newToken.getTokenValue()).isEqualTo(newTokenValue);
        assertThat(newToken.getExpireDate()).isAfter(Instant.now());
        assertThat(newToken.getExpireDate()).isBeforeOrEqualTo(Instant.now().plus(Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("유효한 토큰은 true를 반환한다")
    void 유효한_토큰_검증_성공() {
        // given
        token.setExpireDate(Instant.now().plus(Duration.ofMinutes(10)));

        // when
        boolean isValid = token.isValid();

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰은 false를 반환한다")
    void 만료된_토큰_검증_실패() {
        // given
        token.setExpireDate(Instant.now().minus(Duration.ofMinutes(10)));

        // when
        boolean isValid = token.isValid();

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("tokenValue가 null이면 false를 반환한다")
    void null_tokenValue_검증_실패() {
        // given
        token.setTokenValue(null);
        token.setExpireDate(Instant.now().plus(Duration.ofMinutes(10)));

        // when
        boolean isValid = token.isValid();

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("토큰의 만료 시간이 30분으로 설정된다")
    void 토큰_만료_시간_설정_확인() {
        // given
        Instant now = Instant.now();

        // when
        Token newToken = Token.create(userId, tokenValue);

        // then
        assertThat(newToken.getExpireDate())
            .isAfter(now.plus(Duration.ofMinutes(29)))
            .isBeforeOrEqualTo(now.plus(Duration.ofMinutes(30)));
    }
}
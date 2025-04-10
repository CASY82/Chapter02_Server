package kr.hhplus.be.server.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.token.TokenService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TokenUnitTests {

    private TokenRepository tokenRepository;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        this.tokenRepository = Mockito.mock(TokenRepository.class);
        this.tokenService = new TokenService(this.tokenRepository);
    }

    @Test
    void issueToken_shouldReturnValidJwt() {
        // given
        Long userId = 1L;
        String queueValue = "5";

        doNothing().when(this.tokenRepository).save(any(Token.class));

        // when
        String jwt = this.tokenService.issueToken(userId, queueValue);

        // then
        assertThat(jwt).isNotBlank();
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256)) // 이 키는 일치하지 않음. 그래서 아래 방식으로 대체
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        // -> 대신 추출 메서드 사용
        Long extracted = this.tokenService.extractUserId(jwt);
        assertThat(extracted).isEqualTo(userId);
    }


    @Test
    @DisplayName("토큰을 발급하면 유효한 JWT가 생성된다")
    void 토큰_발급_성공() {
        // given
        Long userId = 1L;
        String queueValue = "1";

        doNothing().when(this.tokenRepository).save(any(Token.class));

        // when
        String jwt = this.tokenService.issueToken(userId, queueValue);

        // then
        assertThat(jwt).isNotBlank();
    }

    @Test
    @DisplayName("유효한 토큰이면 true를 반환한다")
    void 유효한_토큰_검증_성공() {
        // given
        Long userId = 2L;
        String queueValue = "2";
        Token token = new Token(userId, queueValue);

        doNothing().when(this.tokenRepository).save(any(Token.class));
        when(this.tokenRepository.findByTokenId(token.getTokenId())).thenReturn(token);

        String jwt = this.tokenService.issueToken(userId, queueValue);

        // when
        boolean result = this.tokenService.validateTokenValue(jwt);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("토큰이 DB에 없으면 false를 반환한다")
    void 토큰이_DB에_없음() {
        // given
        Long userId = 3L;
        String jwt = this.tokenService.issueToken(userId, "1");

        when(this.tokenRepository.findByTokenId(anyString())).thenReturn(null);

        // when
        boolean result = this.tokenService.validateTokenValue(jwt);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("토큰이 만료되었으면 false를 반환한다")
    void 만료된_토큰_검증_실패() {
        // given
        Long userId = 4L;

        Token expiredToken = new Token(userId, "1") {
            @Override
            public boolean validationCheck() {
                return false;
            }

            @Override
            public Instant getExpireDate() {
                return Instant.now().minusSeconds(60);
            }
        };

        doNothing().when(tokenRepository).save(any(Token.class));
        when(tokenRepository.findByTokenId(expiredToken.getTokenId())).thenReturn(expiredToken);

        String jwt = this.tokenService.issueToken(userId, "1");

        // when
        boolean result = this.tokenService.validateTokenValue(jwt);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("JWT에서 사용자 ID를 정확히 추출할 수 있다")
    void JWT에서_사용자ID_추출() {
        // given
        Long userId = 99L;
        String jwt = this.tokenService.issueToken(userId, "10");

        // when
        Long extracted = this.tokenService.extractUserId(jwt);

        // then
        assertThat(extracted).isEqualTo(userId);
    }
}

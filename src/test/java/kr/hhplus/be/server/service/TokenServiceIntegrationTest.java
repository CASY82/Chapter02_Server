package kr.hhplus.be.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.security.Key;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.domain.user.User;

@SpringBootTest
@ActiveProfiles("test")
public class TokenServiceIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
        // 테스트용 사용자 생성 및 저장
        testUser = new User();
        testUser.setUserId("test-user-1");
        testUser.setUsername("TestUser");
        testUser.setPassword("password123");
    }

    @Test
    public void 유저_토큰_발급_정상_테스트() {
        // given
        Long userRefId = this.testUser.getId();
        String queueValue = "queue-position-1";

        // when
        String jwt = this.tokenService.issueToken(userRefId, queueValue);

        // then
        assertNotNull(jwt, "발급된 JWT 토큰은 null이 아니어야 한다.");

        // JWT에서 tokenId 추출 (validateTokenValue의 로직 재사용)
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // private secretKey 접근을 위해 리플렉션 또는 별도 메서드 필요
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        String tokenId = claims.get("tokenId", String.class);

        Token savedToken = this.tokenRepository.findByTokenId(tokenId);
        
        assertNotNull(savedToken, "DB에 토큰이 저장되어야 한다.");
        assertEquals(userRefId, savedToken.getUserRefId(), "토큰의 userRefId가 일치해야 한다.");
        assertEquals(queueValue, savedToken.getTokenValue(), "토큰의 queueValue가 일치해야 한다.");
        assertTrue(savedToken.getExpireDate().isAfter(Instant.now()), "토큰의 만료 시간이 현재 시간 이후여야 한다.");
        
        boolean isValid = this.tokenService.validateTokenValue(jwt);
        assertTrue(isValid, "발급된 토큰은 유효해야 한다.");
    }

    @Test
    public void 만료된_토큰_유효성_검증_실패_테스트() {
        // given
        Long userRefId = this.testUser.getId();
        String queueValue = "queue-position-2";
        
        // 만료된 토큰 수동 생성
        Token expiredToken = new Token(userRefId, queueValue);
        expiredToken.setExpireDate(Instant.now().minusSeconds(60)); // 만료 시간 과거로 설정
        this.tokenRepository.save(expiredToken);

        // 새로운 토큰 발급
        String jwt = this.tokenService.issueToken(userRefId, queueValue);

        // then
        boolean isValid = this.tokenService.validateTokenValue(jwt);
        assertTrue(isValid, "새로 발급된 토큰은 유효해야 한다.");

        // 만료된 토큰 확인
        Token savedExpiredToken = this.tokenRepository.findByTokenId(expiredToken.getTokenId());
        assertFalse(savedExpiredToken.validationCheck(), "만료된 토큰은 유효하지 않아야 한다.");
    }

    // secretKey 접근을 위한 임시 메서드 (실제로는 TokenService에 추가하거나 다른 방식으로 처리)
    private Key getSigningKey() {
        try {
            Field field = TokenService.class.getDeclaredField("secretKey");
            field.setAccessible(true);
            return (Key) field.get(this.tokenService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access secretKey", e);
        }
    }
}

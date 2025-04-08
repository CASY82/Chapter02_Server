package kr.hhplus.be.server.domain.token;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TokenUnitTests {

    @Test
    public void 토큰_생성() {
        // given & when
        Token t1 = new Token(10L, "10");

        // then
        assertEquals(10L, t1.getUserRefId());
        assertEquals("10", t1.getTokenValue());
        
        // Instant 비교 시 오차 범위 고려
        Instant expected = Instant.now().plus(Duration.ofMinutes(30));
        assertTrue(Duration.between(t1.getExpireDate(), expected).abs().toMinutes() < 1); // 1분 이내 오차 허용
    }
    
    @Test
    public void 토큰_유효성_검증_정상() {
    	// given
        Token t1 = new Token(10L, "10");
        // 30분 전으로 강제 설정
        t1.setExpireDate( Instant.now().minus(Duration.ofMinutes(30)));
        
        // when & then
        assertFalse(t1.validationCheck());
    }
    
    @Test
    public void 토큰_유효성_검증_30분_지난_경우() {
    	// given
        Token t1 = new Token(10L, "10");
        
        // when & then
        assertTrue(t1.validationCheck());
    }
}

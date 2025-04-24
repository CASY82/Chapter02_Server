package kr.hhplus.be.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructure.queue.QueueStore;

@SpringBootTest
@Transactional
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private QueueStore queueStore;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setUserId("testUser");
        testUser.setUsername("testUser123");
        testUser.setPassword("pw");
        testUser.setName("테스터");
        userRepository.save(testUser);
    }

    @Test
    void issueToken_처음발급이면_새로저장되고_반환된다() {
        // when
        Token token = tokenService.issueToken(testUser.getId());

        // then
        assertThat(token).isNotNull();
        assertThat(token.getTokenValue()).isNotBlank();
        assertThat(token.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void issueToken_이미_유효한_토큰이_있으면_기존_토큰_반환한다() {
        // given
        Token token1 = tokenService.issueToken(testUser.getId());

        // when
        Token token2 = tokenService.issueToken(testUser.getId());

        // then
        assertThat(token1.getId()).isEqualTo(token2.getId());
    }

    @Test
    void validateToken_정상_토큰이면_예외_발생하지_않는다() {
        // given
        Token token = tokenService.issueToken(testUser.getId());

        // when & then
        assertThatCode(() -> tokenService.validateToken(token.getTokenValue()))
            .doesNotThrowAnyException();
    }

    @Test
    void validateToken_만료된_토큰이면_예외를_던진다() {
        // given
        Token token = tokenService.issueToken(testUser.getId());
        token.setExpireDate(token.getExpireDate().minusSeconds(1800)); // 강제 만료
        tokenRepository.save(token);

        // when & then
        assertThatThrownBy(() -> tokenService.validateToken(token.getTokenValue()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("expired");
    }

    @Test
    void getQueueStatus_정상토큰이면_포지션과_입장가능여부를_반환한다() {
        // given
        Token token = tokenService.issueToken(testUser.getId());

        when(queueStore.getPosition(testUser.getId())).thenReturn(1);
        when(queueStore.isNowEnterable(testUser.getId(), 3)).thenReturn(true);

        // when
        var response = tokenService.getQueueStatus("Bearer " + token.getTokenValue());

        // then
        assertThat(response.position()).isEqualTo(1);
    }

    @Test
    void getQueueStatus_잘못된_토큰이면_에러_메시지와_position_음수() {
        // when
        var response = tokenService.getQueueStatus("Bearer invalid.jwt.token");

        // then
        assertThat(response.position()).isEqualTo(-1);
    }
}


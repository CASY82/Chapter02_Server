package kr.hhplus.be.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.infrastructure.jpa.repository.TokenJpaRepository;

@SpringBootTest
public class TokenServiceConcurrencyTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;
    
    @Autowired
    private TokenJpaRepository tokenJpaRepository;

    @Test
    void testOnlyOneTokenCreatedForConcurrentRequests() throws InterruptedException {
        // Given
        Long userRefId = 1L;
        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    tokenService.issueToken(userRefId);
                } catch (Exception e) {
                    System.err.println("Exception in thread: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // Then
        List<Token> tokens = tokenJpaRepository.findAll(); // findAll()는 테스트 전용으로 추가되었을 것으로 가정
        long count = tokens.stream()
                .filter(t -> t.getUserRefId().equals(userRefId))
                .count();

        System.out.println("Issued tokens for user " + userRefId + ": " + count);

        // 하나의 토큰만 생성되었어야 함
        assertEquals(1, count, "Only one token should be issued for the user despite concurrent requests");
    }
}



//package kr.hhplus.be.server.service;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import kr.hhplus.be.server.domain.token.Token;
//import kr.hhplus.be.server.domain.token.TokenRepository;
//import kr.hhplus.be.server.domain.token.TokenService;
//
//@SpringBootTest
//public class TokenServiceConcurrencyIssueTest {
//
//    @Autowired
//    private TokenService tokenService;
//
//    @Autowired
//    private TokenRepository tokenRepository;
//
//    @Test
//    @Transactional
//    void testConcurrentTokenIssuanceCausesDuplication() throws InterruptedException {
//        // Given
//        Long userRefId = 1L;
//        String queueValue = "queue-1";
//        int numberOfThreads = 10; // 10개의 스레드가 동시에 토큰 발급 시도
//        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//        CountDownLatch latch = new CountDownLatch(numberOfThreads);
//
//        // When
//        for (int i = 0; i < numberOfThreads; i++) {
//            executorService.submit(() -> {
//                try {
//                    // 동일한 userRefId로 토큰 발급 시도
//                    this.tokenService.issueToken(userRefId, queueValue);
//                } catch (Exception e) {
//                    // 예외는 무시 (동시성 문제 확인이 목적)
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        // 모든 스레드가 완료될 때까지 대기
//        latch.await();
//
//        // Then
//        // 동일한 userRefId에 대해 저장된 토큰 데이터 조회
//        List<Token> tokens = this.tokenRepository.findAll();
//        long tokenCount = tokens.stream()
//                .filter(token -> token.getUserRefId().equals(userRefId))
//                .count();
//
//        // 동시성 문제로 인해 여러 토큰이 발급되었는지 확인
//        assertTrue(tokenCount > 1, "Multiple tokens were issued for the same userRefId due to concurrency issue");
//
//        // 토큰 수 출력 (디버깅용)
//        System.out.println("Number of tokens for userRefId " + userRefId + ": " + tokenCount);
//
//        // ExecutorService 종료
//        executorService.shutdown();
//    }
//}
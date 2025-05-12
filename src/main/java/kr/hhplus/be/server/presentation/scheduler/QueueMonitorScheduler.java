package kr.hhplus.be.server.presentation.scheduler;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.infrastructure.queue.QueueStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueMonitorScheduler {

    private final QueueStore queueStore;
    private final ConcurrentLinkedQueue<Token> pendingQueue = new ConcurrentLinkedQueue<>(); // 대기열 진입 요청 큐

    @Value("${queue.max-enterable:3}")
    private int maxEnterable;

    // 대기열 진입 요청 추가
    public void requestEnterQueue(Token token) {
        pendingQueue.add(token);
        log.info("User {} requested to enter queue", token.getUser().getId());
    }

    // 10초마다 대기열 모니터링 및 TTL 처리
    @Scheduled(fixedRate = 10000)
    public void monitorQueue() {
        log.info("Starting queue monitoring...");

        // 1. 만료된 토큰 제거 (TTL 처리)
        ConcurrentLinkedQueue<Token> queue = queueStore.getQueue();
        queue.removeIf(token -> !token.isValid());
        log.info("Removed expired tokens. Current queue size: {}", queue.size());

        // 2. 대기열 진입 처리
        while (!pendingQueue.isEmpty()) {
            Token token = pendingQueue.poll();
            if (token.isValid()) {
                int position = queueStore.enterQueue(token);
                log.info("User {} entered queue at position {}", token.getUser().getId(), position);
            } else {
                log.info("User {} token expired, skipped queue entry", token.getUser().getId());
            }
        }

        // 3. 입장 가능 여부 확인 및 처리
        for (Token token : queue) {
            if (queueStore.isNowEnterable(token, maxEnterable)) {
                queueStore.leaveQueue(token);
                log.info("User {} removed from queue as they are now enterable", token.getUser().getId());
            }
        }

        log.info("Queue monitoring completed. Current queue size: {}", queue.size());
    }
}
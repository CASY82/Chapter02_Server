package kr.hhplus.be.server.presentation.scheduler;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.infrastructure.queue.QueueStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueMonitorScheduler {
    private final QueueStore queueStore;
    private final ConcurrentLinkedQueue<Token> pendingQueue = new ConcurrentLinkedQueue<>();

    @Value("${queue.max-enterable:3}")
    private int maxEnterable;

    public void requestEnterQueue(Token token) {
        pendingQueue.add(token);
        log.info("User {} requested to enter queue", token.getUserRefId());
    }

    @Scheduled(fixedRate = 10000) // 10초마다
    public void monitorQueue() {
        log.info("Starting queue monitoring...");

        // 대기열 진입 처리
        while (!pendingQueue.isEmpty()) {
            Token token = pendingQueue.poll();
            if (token.isValid()) {
                int position = queueStore.enterQueue(token);
                log.info("User {} entered queue at position {}", token.getUserRefId(), position);
            } else {
                log.info("User {} token expired, skipped queue entry", token.getUserRefId());
            }
        }

        // Redis TTL이 만료 토큰 제거, 입장 가능 여부 확인
        ConcurrentLinkedQueue<Token> queue = queueStore.getQueue();
        for (Token token : queue) {
            if (queueStore.isNowEnterable(token, maxEnterable)) {
                queueStore.leaveQueue(token);
                log.info("User {} removed from queue as they are now enterable", token.getUserRefId());
            }
        }

        log.info("Queue monitoring completed. Current queue size: {}", queue.size());
    }
}
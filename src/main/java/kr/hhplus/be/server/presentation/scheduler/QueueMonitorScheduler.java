package kr.hhplus.be.server.presentation.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.infrastructure.queue.QueueStore;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueMonitorScheduler {

    private final QueueStore queueStore;

    @Value("${queue.max-enterable:3}")
    private int maxEnterable;

    @Scheduled(fixedRate = 10000)
    public void monitorQueue() {
        log.info("Starting queue monitoring...");
        ConcurrentLinkedQueue<Long> queue = queueStore.getQueue();
        for (Long userId : queue) {
            if (queueStore.isNowEnterable(userId, maxEnterable)) {
                queueStore.leaveQueue(userId);
                log.info("User {} removed from queue as they are now enterable", userId);
            }
        }
        log.info("Queue monitoring completed. Current queue size: {}", queue.size());
    }
}

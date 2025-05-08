package kr.hhplus.be.server.infrastructure.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface QueueStore {
    int enterQueue(Long userId);
    int getPosition(Long userId);
    void leaveQueue(Long userId);
    boolean isNowEnterable(Long userId, int threshold);
    ConcurrentLinkedQueue<Long> getQueue(); // 대기열 반환 메서드 추가
}
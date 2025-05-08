package kr.hhplus.be.server.infrastructure.queue.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import kr.hhplus.be.server.infrastructure.queue.QueueStore;
import org.springframework.stereotype.Component;

@Component
public class InMemoryQueueStore implements QueueStore {

    private final Queue<Long> queue = new ConcurrentLinkedQueue<>();

    @Override
    public synchronized int enterQueue(Long userId) {
        if (!queue.contains(userId)) {
            queue.add(userId);
        }
        return getPosition(userId);
    }

    @Override
    public int getPosition(Long userId) {
        int pos = 1;
        for (Long uid : queue) {
            if (uid.equals(userId)) return pos;
            pos++;
        }
        return -1;
    }

    @Override
    public void leaveQueue(Long userId) {
        queue.remove(userId);
    }

    @Override
    public boolean isNowEnterable(Long userId, int threshold) {
        return getPosition(userId) <= threshold;
    }

    @Override
    public ConcurrentLinkedQueue<Long> getQueue() {
        return (ConcurrentLinkedQueue<Long>) queue; // queue 반환
    }
}
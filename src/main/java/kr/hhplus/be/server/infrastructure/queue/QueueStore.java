package kr.hhplus.be.server.infrastructure.queue;

public interface QueueStore {
    int enterQueue(Long userId);
    int getPosition(Long userId);
    void leaveQueue(Long userId);
    boolean isNowEnterable(Long userId, int threshold);
}

package kr.hhplus.be.server.infrastructure.queue;

import kr.hhplus.be.server.domain.token.Token;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface QueueStore {
    int enterQueue(Token token);
    int getPosition(Token token);
    void leaveQueue(Token token);
    boolean isNowEnterable(Token token, int threshold);
    ConcurrentLinkedQueue<Token> getQueue(); // Token 기반 대기열 반환
}
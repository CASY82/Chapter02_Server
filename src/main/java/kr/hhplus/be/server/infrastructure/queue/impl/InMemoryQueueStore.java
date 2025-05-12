package kr.hhplus.be.server.infrastructure.queue.impl;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.infrastructure.queue.QueueStore;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class InMemoryQueueStore implements QueueStore {

    private final Queue<Token> queue = new ConcurrentLinkedQueue<>();

    @Override
    public synchronized int enterQueue(Token token) {
        if (!queue.contains(token)) {
            queue.add(token);
        }
        return getPosition(token);
    }

    @Override
    public int getPosition(Token token) {
        int pos = 1;
        for (Token t : queue) {
            if (t.equals(token)) return pos;
            pos++;
        }
        return -1;
    }

    @Override
    public void leaveQueue(Token token) {
        queue.remove(token);
    }

    @Override
    public boolean isNowEnterable(Token token, int threshold) {
        return getPosition(token) <= threshold && token.isValid();
    }

    @Override
    public ConcurrentLinkedQueue<Token> getQueue() {
        return (ConcurrentLinkedQueue<Token>) queue;
    }
}
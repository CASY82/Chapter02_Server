package kr.hhplus.be.server.infrastructure.queue.impl;

import kr.hhplus.be.server.domain.token.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class RedisQueueStore {
    private static final String QUEUE_KEY = "queue:pending";
    private static final String ENTERABLE_KEY = "queue:enterable";

    private final RedisTemplate<String, String> redisTemplate;

    public int enterQueue(Token token) {
        redisTemplate.opsForList().leftPush(QUEUE_KEY, token.getTokenValue());
        return getPosition(token);
    }

    public int getPosition(Token token) {
        List<String> queue = redisTemplate.opsForList().range(QUEUE_KEY, 0, -1);
        if (queue == null) {
            return -1;
        }
        int position = queue.indexOf(token.getTokenValue());
        return position >= 0 ? position + 1 : -1;
    }

    public void leaveQueue(Token token) {
        redisTemplate.opsForList().remove(QUEUE_KEY, 1, token.getTokenValue());
    }

    public boolean isNowEnterable(Token token, int threshold) {
        int position = getPosition(token);
        if (position <= 0) {
            return false;
        }
        if (position <= threshold) {
            // 입장 가능 Set에 추가
            redisTemplate.opsForSet().add(ENTERABLE_KEY, token.getTokenValue());
            return true;
        }
        return redisTemplate.opsForSet().isMember(ENTERABLE_KEY, token.getTokenValue());
    }

    // 인메모리 큐 제거, Redis List 반환 대체
    public ConcurrentLinkedQueue<Token> getQueue() {
        List<String> tokenValues = redisTemplate.opsForList().range(QUEUE_KEY, 0, -1);
        ConcurrentLinkedQueue<Token> queue = new ConcurrentLinkedQueue<>();
        if (tokenValues != null) {
            for (String tokenValue : tokenValues) {
                queue.add(Token.builder().tokenValue(tokenValue).build());
            }
        }
        return queue;
    }
}
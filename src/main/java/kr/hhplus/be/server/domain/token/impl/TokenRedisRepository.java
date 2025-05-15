package kr.hhplus.be.server.domain.token.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepository implements TokenRepository {
	 private static final String TOKEN_KEY_PREFIX = "token:";
	    private static final long TOKEN_TTL_SECONDS = 1800; // 30분

	    private final RedisTemplate<String, String> redisTemplate;
	    private final ObjectMapper objectMapper;

	    public Token save(Token token) {
	        try {
	            String key = TOKEN_KEY_PREFIX + token.getTokenValue();
	            String json = objectMapper.writeValueAsString(token);
	            redisTemplate.opsForValue().set(key, json);
	            redisTemplate.expire(key, TOKEN_TTL_SECONDS, TimeUnit.SECONDS);
	            return token;
	        } catch (Exception e) {
	            throw new RuntimeException("Failed to save token to Redis", e);
	        }
	    }

	    public Optional<Token> findByTokenValue(String tokenValue) {
	        try {
	            String key = TOKEN_KEY_PREFIX + tokenValue;
	            String json = redisTemplate.opsForValue().get(key);
	            if (json == null) {
	                return Optional.empty();
	            }
	            return Optional.of(objectMapper.readValue(json, Token.class));
	        } catch (Exception e) {
	            throw new RuntimeException("Failed to find token by tokenValue", e);
	        }
	    }

	    public Optional<Token> findByUserRefIdWithLock(Long userRefId) {
	        // Redis에서는 락 구현이 필요하면 Redisson 또는 Redis SETNX 사용
	        // 현재는 단순 조회로 대체 (필요 시 락 추가)
	        try {
	            for (String key : redisTemplate.keys(TOKEN_KEY_PREFIX + "*")) {
	                String json = redisTemplate.opsForValue().get(key);
	                if (json != null) {
	                    Token token = objectMapper.readValue(json, Token.class);
	                    if (token.getUserRefId().equals(userRefId) && token.isValid()) {
	                        return Optional.of(token);
	                    }
	                }
	            }
	            return Optional.empty();
	        } catch (Exception e) {
	            throw new RuntimeException("Failed to find token by userRefId", e);
	        }
	    }
}

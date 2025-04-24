package kr.hhplus.be.server.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.hhplus.be.server.infrastructure.queue.QueueStore;
import kr.hhplus.be.server.presentation.api.v1.token.TokenController.QueueStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final QueueStore queueStore;
    private static final String SECRET_KEY = "your-256-bit-secret-key-for-jwt-signing";
    private static final long TOKEN_EXPIRY_MS = 30 * 60 * 1000; // 30분

    @Value("${queue.max-enterable:3}")
    private int maxEnterable;

    public Token issueToken(Long userRefId) {
        String tokenValue = generateJwt(userRefId);
        Token token = Token.create(userRefId, tokenValue);
        return tokenRepository.save(token);
    }

    public void validateToken(String tokenValue) {
        Token token = tokenRepository.findByTokenValue(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (!token.isValid()) {
            throw new IllegalArgumentException("Token is expired or invalid");
        }
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(tokenValue);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }

    public QueueStatusResponse getQueueStatus(String authorization) {
        // JWT에서 userRefId 추출
        String tokenValue = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(tokenValue).getBody();
        } catch (JwtException e) {
            return new QueueStatusResponse(-1, false, "Invalid JWT token: " + e.getMessage());
        }
        Long userRefId = Long.valueOf(claims.getSubject());

        // 대기열 상태 확인
        int position = queueStore.getPosition(userRefId);
        boolean enterable = queueStore.isNowEnterable(userRefId, maxEnterable);

        return new QueueStatusResponse(position, enterable);
    }

    private String generateJwt(Long userRefId) {
        return Jwts.builder()
                .setSubject(userRefId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRY_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}

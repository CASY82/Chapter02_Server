package kr.hhplus.be.server.domain.token;

import java.util.Optional;

public interface TokenRepository {
    Token save(Token token);
    Optional<Token> findByTokenValue(String tokenValue);
    Optional<Token> findByUserRefIdWithLock(Long userRefId);
}
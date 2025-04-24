package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.TokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenDomainRepository implements TokenRepository {

    private final TokenJpaRepository repository;

    @Override
    public Token save(Token token) {
        return repository.save(token);
    }

    @Override
    public Optional<Token> findByTokenValue(String tokenValue) {
        return repository.findByTokenValue(tokenValue);
    }
}

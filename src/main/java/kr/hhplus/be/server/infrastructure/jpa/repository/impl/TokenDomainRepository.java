package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.TokenJpaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenDomainRepository implements TokenRepository {

	private final TokenJpaRepository repository;
	
	@Override
	public void save(Token token) {
		this.repository.save(token);
	}

	@Override
	public Token findByTokenId(String tokenId) {
		return this.repository.findByTokenId(tokenId);
	}

	@Override
	public Token findByUserRefId(Long userRefId) {
		return this.repository.findByUserRefId(userRefId);
	}

}

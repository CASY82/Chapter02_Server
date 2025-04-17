package kr.hhplus.be.server.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.token.Token;


public interface TokenJpaRepository extends JpaRepository<Token, Long>{
	Token findByTokenId(String tokenId);
	Token findByUserRefId(Long userRefId);
}

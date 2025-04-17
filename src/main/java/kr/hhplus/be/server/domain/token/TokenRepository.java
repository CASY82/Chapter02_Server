package kr.hhplus.be.server.domain.token;

import java.util.List;

public interface TokenRepository {
	void save(Token token);
	Token findByTokenId(String tokenId);
	Token findByUserRefId(Long userRefId);
	List<Token> findAll();
}

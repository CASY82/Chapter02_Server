package kr.hhplus.be.server.domain.token;

public interface TokenRepository {
	void save(Token token);
	Token findByTokenId(String tokenId);
	Token findByUserRefId(Long userRefId);
}

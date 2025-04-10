package kr.hhplus.be.server.domain.token;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
	
	private final TokenRepository repository;
	private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	
	public String generateToken(Long userRefId, String queueValue) {
		Token token = new Token(userRefId, queueValue);
		this.repository.save(token);
		
		return Jwts.builder()
            .setSubject("queueToken")
            .claim("position", token.getTokenValue())
            .claim("tokenId", token.getTokenId())
            .setExpiration(Date.from(token.getExpireDate()))
            .signWith(this.secretKey)
            .compact();
	}
	
	public boolean validateTokenValue(String jwt) {
	    try {
	      Claims claims = Jwts.parserBuilder()
	              .setSigningKey(this.secretKey)
	              .build()
	              .parseClaimsJws(jwt)
	              .getBody();
	          
	      String tokenId = claims.get("tokenId", String.class);
	      Token token = this.repository.findByTokenId(tokenId);
	
	      if (token == null) {
	        log.warn("Token not found in DB | tokenId: {}", tokenId);
	        return false;
	      }
	
	      boolean isValid = token.validationCheck();
	
	      if (!isValid) {
	        log.warn("Token expired for tokenId: {}", tokenId);
	      }
	
	      return isValid;
	    } catch (Exception e) {
	      log.error("JWT validation failed: {}", e.getMessage(), e);
	      return false;
	    }
	}
}

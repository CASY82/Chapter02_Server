package kr.hhplus.be.server.domain.token;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
	private Long id;
	private String tokenId; // 유저의 UUID
	private Long userRefId;
	private String tokenValue; // 대기열 관리 정보(대기 순서)
	private Instant expireDate;
	private Instant createdAt;
	private Instant updatedAt;
	
	// 새로운 토큰 생성
    public Token(Long userRefId, String queueValue) {
    	UUID queueTokenId = UUID.randomUUID();
    	
    	this.setTokenId(String.valueOf(queueTokenId));
    	this.setUserRefId(userRefId);
    	this.setTokenValue(queueValue);
    	this.setExpireDate(this.expirePolicy());
    }
    
    // 토큰 유효성 검증
    public boolean validationCheck() {
    	return this.expireDate.isAfter(Instant.now());
    }
    
    // 만료 시간 정책
    public Instant expirePolicy() {
    	return Instant.now().plus(Duration.ofMinutes(30));
    }
}

package kr.hhplus.be.server.domain.token;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
public class Token extends BaseEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false, unique = true)
    private String tokenId; // 유저의 UUID

    @Column(name = "user_ref_id", nullable = false)
    private Long userRefId;

    @Column(name = "token_value", nullable = false)
    private String tokenValue; // 대기열 관리 정보(대기 순서)

    @Column(name = "expire_date", nullable = false)
    private Instant expireDate;
	
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

package kr.hhplus.be.server.domain.token;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "token")
public class Token extends BaseEntity {

	private static final Duration TOKEN_EXPIRY = Duration.ofMinutes(30);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false, unique = true)
    private String tokenId; // 토큰의 유니크 식별자 (UUID)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_ref_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "token_value", nullable = false)
    private String tokenValue; // 대기열 관리 정보 (예: JWT 또는 대기 순서)

    @Column(name = "expire_date", nullable = false)
    private Instant expireDate;

    // 정적 팩토리 메서드
    public static Token create(Long userRefId, String tokenValue) {
        User user = new User();
        user.setId(userRefId);
        return Token.builder()
                .tokenId(UUID.randomUUID().toString())
                .user(user)
                .tokenValue(tokenValue)
                .expireDate(Instant.now().plus(TOKEN_EXPIRY))
                .build();
    }

    // 토큰 유효성 검증
    public boolean isValid() {
        return tokenValue != null && expireDate.isAfter(Instant.now());
    }
}

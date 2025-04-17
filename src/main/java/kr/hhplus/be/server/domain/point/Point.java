package kr.hhplus.be.server.domain.point;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "points")
public class Point extends BaseEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_ref_id", nullable = false)
    private Long userRefId;

    @Column(name = "remain_point", nullable = false)
    private Integer remainPoint;
	
	// 포인트 충전
	public void charge(int amount) {
		if (amount < 0) {
			log.warn("포인트는 음수일 수 없습니다.");
			return;
		}
		
		this.remainPoint += amount;
	}
	
	// 포인트 사용
	public void use(int amount) {
		if (amount < 0) {
			log.warn("포인트는 음수일 수 없습니다.");
			return;
		}
		
		this.remainPoint -= amount;
	}
}

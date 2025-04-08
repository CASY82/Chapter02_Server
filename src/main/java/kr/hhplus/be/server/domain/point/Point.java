package kr.hhplus.be.server.domain.point;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {
	private Long id;
	private Long userRefId;
	private Integer remainPoint;
	private Instant createdAt;
	private Instant updatedAt;
	
	// 포인트 충전
	public void chargePoint(int amount) {
		if (amount < 0) {
			log.warn("포인트는 음수일 수 없습니다.");
			return;
		}
		
		this.remainPoint += amount;
	}
	
	// 포인트 사용
	public void usePoint(int amount) {
		if (amount < 0) {
			log.warn("포인트는 음수일 수 없습니다.");
			return;
		}
		
		this.remainPoint -= amount;
	}
}

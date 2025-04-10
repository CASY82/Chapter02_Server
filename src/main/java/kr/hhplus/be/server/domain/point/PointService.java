package kr.hhplus.be.server.domain.point;

import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {
	
	private final PointRepository repository;
	
	// 포인트 충전
	public Integer chargePoint(Long userRefId, int amount) {
		if (Objects.isNull(userRefId) || userRefId <= 0) {
			throw new IllegalArgumentException("없는 계정입니다.");
		}
		
		Point point = this.repository.findByUserRefId(userRefId);
		
		point.charge(amount);
		
		this.repository.save(point);
		
		return point.getRemainPoint();
	}
	
	// 포인트 사용
	public Integer usePoint(Long userRefId, int amount) {
		if (Objects.isNull(userRefId) || userRefId <= 0) {
			throw new IllegalArgumentException("없는 계정입니다.");
		}
		
		Point point = this.repository.findByUserRefId(userRefId);
		
		point.use(amount);
		
		this.repository.save(point);
		
		return point.getRemainPoint();
	}
	
	// 잔액 조회
	public Integer getPoint(Long userRefId) {
		if (userRefId <= 0) {
			throw new IllegalArgumentException("없는 계정입니다.");
		}
		
		Point userPoint = this.repository.findByUserRefId(userRefId);
		
		return userPoint.getRemainPoint();
	}
}

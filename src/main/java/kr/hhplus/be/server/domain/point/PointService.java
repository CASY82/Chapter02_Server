package kr.hhplus.be.server.domain.point;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointService {
	
	private final PointRepository repository;
	
	// 포인트 충전
	public void chargePoint(Long userRefId, int amount) {
		if (userRefId <= 0) {
			throw new IllegalArgumentException("없는 계정입니다.");
		}
		
		Point point = this.repository.findByUserRefId(userRefId);
		
		point.charge(amount);
		
		this.repository.save(point);
	}
	
	// 포인트 충전
	public void usePoint(Long userRefId, int amount) {
		if (userRefId <= 0) {
			throw new IllegalArgumentException("없는 계정입니다.");
		}
		
		Point point = this.repository.findByUserRefId(userRefId);
		
		point.use(amount);
		
		this.repository.save(point);
	}
}

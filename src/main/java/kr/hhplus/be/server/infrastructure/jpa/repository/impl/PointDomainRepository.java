package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.PointJpaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointDomainRepository implements PointRepository {
	
	private final PointJpaRepository repository;

	@Override
	public void save(Point point) {
		this.repository.save(point);
	}

	@Override
	public Point findByUserRefId(Long userRefId) {
		return this.findByUserRefId(userRefId);
	}

}

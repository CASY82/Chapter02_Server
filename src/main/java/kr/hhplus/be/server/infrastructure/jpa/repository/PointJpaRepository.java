package kr.hhplus.be.server.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.point.Point;


public interface PointJpaRepository extends JpaRepository<Point, Long> {
	Point findByUserRefId(Long userRefId);
}

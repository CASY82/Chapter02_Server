package kr.hhplus.be.server.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.performance.Performance;

public interface PerformanceJpaRepository extends JpaRepository<Performance, Long> {
	Performance findByPerformanceId(Long performanceId);
}

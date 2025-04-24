package kr.hhplus.be.server.infrastructure.jpa.repository;

import kr.hhplus.be.server.domain.performance.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerformanceJpaRepository extends JpaRepository<Performance, Long> {
    Optional<Performance> findByPerformanceId(Long performanceId);
}
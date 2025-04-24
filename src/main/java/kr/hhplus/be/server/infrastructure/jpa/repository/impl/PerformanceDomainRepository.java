package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import kr.hhplus.be.server.domain.performance.Performance;
import kr.hhplus.be.server.domain.performance.PerformanceRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.PerformanceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PerformanceDomainRepository implements PerformanceRepository {

    private final PerformanceJpaRepository repository;

    @Override
    public Performance findById(Long performanceId) {
        return repository.findByPerformanceId(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("Performance not found with performanceId: " + performanceId));
    }
}
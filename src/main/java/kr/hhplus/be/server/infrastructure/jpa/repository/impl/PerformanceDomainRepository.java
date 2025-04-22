package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.performance.Performance;
import kr.hhplus.be.server.domain.performance.PerformanceRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.PerformanceJpaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PerformanceDomainRepository implements PerformanceRepository {

	private final PerformanceJpaRepository repository;
	
	@Override
	public List<Performance> findAllPerformance() {
		return this.repository.findAll();
	}

	@Override
	public Performance findById(Long performanceId) {
		return this.repository.findByPerformanceId(performanceId);
	}

}

package kr.hhplus.be.server.domain.performance;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {
	
	private final PerformanceRepository repository;
	
	public Performance getPerformanceData(Long performanceId) {
		return this.repository.findById(performanceId);
	}
}

package kr.hhplus.be.server.domain.performance;

import java.util.List;

public interface PerformanceRepository {
	List<Performance> findAllPerformance();
	Performance findById(Long performanceId);
}

package kr.hhplus.be.server.domain.performance;

public interface PerformanceRepository {
    Performance findById(Long performanceId);
}
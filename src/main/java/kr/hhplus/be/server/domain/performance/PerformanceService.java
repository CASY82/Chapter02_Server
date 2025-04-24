package kr.hhplus.be.server.domain.performance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceService {
    private final PerformanceRepository performanceRepository;

    public Performance getPerformance(Long performanceId) {
        return performanceRepository.findById(performanceId);
    }
}
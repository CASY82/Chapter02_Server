package kr.hhplus.be.server.application.facade;

import java.util.Objects;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.obj.ReservationCheckCommand;
import kr.hhplus.be.server.application.obj.ReservationCheckResult;
import kr.hhplus.be.server.domain.performance.Performance;
import kr.hhplus.be.server.domain.performance.PerformanceService;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;

/**
 * 예약 파사드
 * 1. 예약 가능 날짜 확인
 * 2. 예약 진행
 */
@Component
@RequiredArgsConstructor
public class ReservationFacade {
	
	private final PerformanceService performanceService;
    private final ScheduleService scheduleService;
	
	public ReservationCheckResult getAvailableSchedules(ReservationCheckCommand command) {
		ReservationCheckResult result = new ReservationCheckResult();
		
        // 공연 존재 여부 확인
        Performance performance = performanceService.getPerformance(command.getPerformanceId());

        if(Objects.isNull(performance)) {
        	throw new IllegalArgumentException("Can't found Performance");
        }
        
        result.setScheduleList(this.scheduleService.getAvailableSchedules(command.getPerformanceId()));
        
        // 예약 가능 일정 조회
        return result;
    }
}


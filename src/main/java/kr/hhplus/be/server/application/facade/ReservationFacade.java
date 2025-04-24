package kr.hhplus.be.server.application.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.performance.PerformanceService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import kr.hhplus.be.server.domain.seat.SeatService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationFacade {
	
	private final PerformanceService performanceService;
    private final ScheduleService scheduleService;
	
	public List<Schedule> getAvailableSchedules(Long performanceId) {
        // 공연 존재 여부 확인
        this.performanceService.getPerformance(performanceId);

        // 예약 가능 일정 조회
        return this.scheduleService.getAvailableSchedules(performanceId);
    }
}


package kr.hhplus.be.server.presentation.api.v1.reserve;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.application.facade.SeatReservationFacade;
import kr.hhplus.be.server.application.obj.ReservationCheckCommand;
import kr.hhplus.be.server.application.obj.ReservationCheckResult;
import kr.hhplus.be.server.presentation.api.v1.obj.ScheduleResponse;
import kr.hhplus.be.server.presentation.api.v1.obj.SeatResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationController {
	
	private final ReservationFacade reservationFacade;
	private final SeatReservationFacade seatReservationFacade;
	
	/**
	 * 예약 가능한 좌석 목록 조회
	 * @param scheduleId
	 * @param userId
	 * @return
	 */
    @GetMapping("/reservations/available/seat")
    public ResponseEntity<SeatResponse> getAvailableSeat(
            @RequestParam("scheduleId") @Positive(message = "Schedule ID must be positive") Long scheduleId,
            @RequestParam("userId") @Positive(message = "User ID must be positive") Long userId) {
    	ReservationCheckCommand command = new ReservationCheckCommand();
    	
    	command.setScheduleId(scheduleId);
    	
        try {
            ReservationCheckResult result = this.seatReservationFacade.getAvailableSeatIds(command);

            SeatResponse response = new SeatResponse();
            response.setSeatIds(result.getSeatIds());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
	
	/**
	 * 예약 가능한 날짜 목록 조회
	 * @param performanceId
	 * @param userId
	 * @return
	 */
    @GetMapping("/reservations/available/schedule")
    public ResponseEntity<ScheduleResponse> getAvailableSchedule(
            @RequestParam("performanceId") @Positive(message = "Performance ID must be positive") Long performanceId,
            @RequestParam("userId") @Positive(message = "User ID must be positive") Long userId) {
    	ReservationCheckCommand command = new ReservationCheckCommand();
    	
    	command.setScheduleId(performanceId);
    	
        try {
        	ReservationCheckResult result = this.reservationFacade.getAvailableSchedules(command);

            ScheduleResponse response = new ScheduleResponse();
            response.setSchedules(result.getScheduleList().stream()
                    .map(schedule -> {
                        ScheduleResponse.ScheduleInfo info = new ScheduleResponse.ScheduleInfo();
                        info.setScheduleId(schedule.getScheduleId());
                        info.setScheduleDateTime(schedule.getScheduleDateTime());
                        return info;
                    })
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
	
}

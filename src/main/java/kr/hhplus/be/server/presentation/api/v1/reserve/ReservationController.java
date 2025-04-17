package kr.hhplus.be.server.presentation.api.v1.reserve;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.presentation.api.v1.obj.ReservationRequest;
import kr.hhplus.be.server.presentation.api.v1.obj.ScheduleResponse;
import kr.hhplus.be.server.presentation.api.v1.obj.SeatResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationController {
	
	private final SeatService seatService;
	private final ScheduleService scheduleService;
	private final ReservationService reservationService;
	private final ReservationFacade reservationFacade;
	
	/**
	 * 예약 가능한 날짜 선택 후, 예약 가능한 좌석
	 * @param scheduleRefId
	 * @return
	 */
	@GetMapping("/reservations/available/seat")
	public ResponseEntity<SeatResponse> getAvailableSeat(@RequestParam("scheduleRefId") long scheduleRefId) {
		if (scheduleRefId < 0) return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(null);
		
		List<Reservation> reservationList = this.reservationService.getReservationList(scheduleRefId);
		
		List<Long> reservedSeatIds = reservationList.stream()
                .map(Reservation::getSeatRefId)
                .collect(Collectors.toList());
		
		List<Seat> seatIds = this.seatService.getSeatList(scheduleRefId);
		
		List<Long> availableSeats = seatIds.stream()
				.map(Seat::getId)
                .filter(seatId -> !reservedSeatIds.contains(seatId))
                .collect(Collectors.toList());

        // SeatResponse 객체 생성 및 seatIds 설정
        SeatResponse seatResponse = new SeatResponse();
        seatResponse.setSeatIds(availableSeats);
		
		return ResponseEntity.ok(seatResponse);
	}
	
	/**
	 * 예약 가능한 날짜 목록 조회
	 * 요구 사항 외 도메인의 식별자 값들은 외부에서 받는다는 전제로 구현
	 * @param performanceId
	 * @param venueId
	 * @return
	 */
	@GetMapping("/reservations/available/schedule")
	public ResponseEntity<ScheduleResponse> getAvailableSchedule(@RequestParam("performanceId") long performanceId,
															     @RequestParam("venueId") long venueId) {
		
		if (performanceId < 0) return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(null);
		if (venueId < 0) return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(null);
		
		List<Schedule> scheduleList = this.scheduleService.getScheduleList(performanceId, venueId);
		
		List<String> dateList = scheduleList.stream()
				.map(Schedule::getDate)
				.collect(Collectors.toList());
		
		ScheduleResponse scheduleResponse = new ScheduleResponse();
		scheduleResponse.setDateList(dateList);
		
		return ResponseEntity.ok(scheduleResponse);
	}
	
	@PostMapping("/reservations")
	public ResponseEntity<Boolean> reserve(@RequestBody ReservationRequest request) {
		this.reservationFacade.reserveSeat(request.getScheduleId(), request.getSeatId(), request.getUserId());
		
		return ResponseEntity.ok(true);
	}
	
}

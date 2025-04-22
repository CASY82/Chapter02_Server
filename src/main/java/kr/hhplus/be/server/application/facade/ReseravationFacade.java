package kr.hhplus.be.server.application.facade;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seat.SeatService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReseravationFacade {
	
	private final ReservationService reservationService;
	private final SeatService seatService;	
	
	@Transactional
	public void reserveSeat(Long scheduleId, Long seatId, Long userId) {
		// 예약 작업을 진행한뒤 상태를 변경
		Reservation reservation = new Reservation();
		
		reservation.setReserveStatus(ReservationStatus.READY);
		reservation.setScheduleRefId(scheduleId);
		reservation.setSeatRefId(seatId);
		reservation.setUserRefId(userId);
		
		this.reservationService.reserveSeat(reservation);
		// TODO: 에약된 좌석과 공연장 등에 대해서 데이터를 긁어온 다음 저장(데이터 만 모아서)
	}
}


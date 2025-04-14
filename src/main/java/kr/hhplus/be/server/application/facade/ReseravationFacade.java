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
		Reservation reservation = new Reservation();
		
		reservation.setReserveStatus(ReservationStatus.READY.name());
		reservation.setScheduleRefId(scheduleId);
		reservation.setSeatRefId(seatId);
		reservation.setUserRefId(userId);
		
		this.seatService.reserveSeat(seatId);
		
		this.reservationService.reserve(reservation);
		
	}
}


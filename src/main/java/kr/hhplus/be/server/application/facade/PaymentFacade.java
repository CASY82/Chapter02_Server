package kr.hhplus.be.server.application.facade;

import java.time.Instant;

import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
	
	private final PaymentService paymentService;
	private final PointService pointService;
	private final ReservationService reservationService;
	private final SeatService seatService;
	
	@Transactional
	public boolean paymentProcess(long userId, int amount) {
		Payment payment = new Payment();
		Reservation reservation = this.reservationService.getReservation(userId);
		if(this.seatService.confirmSeat(userId)) {
			Seat confirmSeat = this.seatService.getSeat(reservation.getSeatRefId());
			
			this.pointService.usePoint(userId, amount);
			
			payment.setAmount(amount);
			payment.setPaymentDate(Instant.now());
			
			reservation.setReserveStatus(ReservationStatus.COMPLETE.name());
			this.reservationService.save(reservation);
			this.paymentService.save(payment);
			
			return true;
		} else {
			return false;
		}
	}

}

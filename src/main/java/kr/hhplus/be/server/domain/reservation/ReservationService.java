package kr.hhplus.be.server.domain.reservation;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reservationRepository;
	
	public void reserveSeat(Reservation reservation) {
		reservation.reserve();
		this.reservationRepository.save(reservation);
	}
	
	public void cancleSeat(Reservation reservation) {
		reservation.cancle();
		this.reservationRepository.save(reservation);
	}
	
	public Reservation getReservationByUser(Long userId) {
		return this.reservationRepository.findByUserRefId(userId);
	}
	
	public boolean isReserve(Long reservationId) {
		Reservation reservation = this.reservationRepository.findById(reservationId);
		return reservation.isReservable();
	}
	
	public List<Reservation> getReservationList(Long scheduleRefId) {
		return this.reservationRepository.findAllReservedSeat(scheduleRefId);
	}

}



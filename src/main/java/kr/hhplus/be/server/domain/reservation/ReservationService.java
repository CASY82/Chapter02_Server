package kr.hhplus.be.server.domain.reservation;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reservationRepository;
	
	public void save(Reservation reservation) {
		this.reservationRepository.save(reservation);
	}
	
	public Reservation getReservation(Long userId) {
		return this.reservationRepository.findByUserRefId(userId);
	}

}



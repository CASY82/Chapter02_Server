package kr.hhplus.be.server.domain.reservation;

import java.util.List;

public interface ReservationRepository {
	void save(Reservation reservation);
	Reservation findByUserRefId(Long userRefId);
	Reservation findById(Long reservationId);
	List<Reservation> findAllAvailableSeat(Long scheduleRefId);
	List<Reservation> findAllReservedSeat(Long scheduleRefId);
}

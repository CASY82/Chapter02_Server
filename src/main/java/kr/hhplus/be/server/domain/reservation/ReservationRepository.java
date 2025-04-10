package kr.hhplus.be.server.domain.reservation;

public interface ReservationRepository {
	void save(Reservation reservation);
	Reservation findByUserRefId(Long userRefId);
}

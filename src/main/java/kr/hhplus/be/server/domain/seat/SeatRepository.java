package kr.hhplus.be.server.domain.seat;

import java.util.List;

public interface SeatRepository {
	void save(Seat seat);
	List<Seat> findAllAvailableSeat(Long scheduleRefId);
	List<Seat> findAllReservedSeat(Long scheduleRefId);
	Seat findById(Long seatId);
	Seat findByUserRefId(Long userRefId);
}

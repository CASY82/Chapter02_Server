package kr.hhplus.be.server.infrastructure.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.seat.Seat;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
	List<Seat> findAllByScheduleRefId(Long scheduleRefId);
	Seat findByUserRefId(Long userRefId);
	Seat findBySeatId(Long seatId);
}

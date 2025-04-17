package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.SeatJpaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatDomainRepository implements SeatRepository {

	private final SeatJpaRepository repository;
	
	@Override
	public void save(Seat seat) {
		this.repository.save(seat);
	}

	@Override
	public Seat findById(Long seatId) {
		return this.repository.findBySeatId(seatId);
	}

	@Override
	public Seat findByUserRefId(Long userRefId) {
		return this.repository.findByUserRefId(userRefId);
	}

	@Override
	public List<Seat> findAllSeat(Long scheduleRefId) {
		return this.repository.findAllByScheduleRefId(scheduleRefId);
	}

}

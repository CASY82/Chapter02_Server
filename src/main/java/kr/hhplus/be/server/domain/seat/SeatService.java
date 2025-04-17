package kr.hhplus.be.server.domain.seat;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {

	private final SeatRepository repository;

	public List<Seat> getSeatList(Long scheduleRefId){
		return this.repository.findAllSeat(scheduleRefId);
	}
	
	public Seat getSeat(Long seatId) {
		return this.repository.findById(seatId);
	}
}


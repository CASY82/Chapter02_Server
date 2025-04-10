package kr.hhplus.be.server.domain.seat;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {

	private final SeatRepository repository;

	public void reserveSeat(Long seatId) {
		Seat seat = this.repository.findById(seatId);
		
		if (!seat.isReservable()) {
			throw new IllegalStateException("이미 예약된 좌석입니다.");
		}
		
		seat.reserve();
		this.repository.save(seat);
	}

	public void releaseSeat(Long seatId) {
		Seat seat = this.repository.findById(seatId);
		seat.unmarkReserved();
		this.repository.save(seat);
	}
	
	public List<Seat> getAvailableSeatList(Long scheduleRefId){
		return this.repository.findAllAvailableSeat(scheduleRefId);
	}
	
	public List<Seat> getReservedSeatList(Long scheduleRefId) {
		return this.repository.findAllReservedSeat(scheduleRefId);
	}
	
	public boolean confirmSeat(Long userId) {
		Seat seat = this.repository.findByUserRefId(userId);
		
		return seat.isReservable();
	}
	
	public Seat getSeat(Long seatId) {
		return this.repository.findById(seatId);
	}
}


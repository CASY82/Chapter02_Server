package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.infrastructure.jpa.repository.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationDomainRepository implements ReservationRepository {
	
	private final ReservationJpaRepository repository;

	@Override
	public void save(Reservation reservation) {
		this.repository.save(reservation);
	}

	@Override
	public Reservation findByUserRefId(Long userRefId) {
		return this.findByUserRefId(userRefId);
	}

	@Override
	public Reservation findById(Long reservationId) {
		return this.repository.findById(reservationId).orElseThrow(() -> new RuntimeException("예약이 없습니다."));
	}

	@Override
	public List<Reservation> findAllAvailableSeat(Long scheduleRefId) {
        return this.repository.findAllByReserveStatusAndScheduleRefId(ReservationStatus.READY, scheduleRefId);
	}

	@Override
	public List<Reservation> findAllReservedSeat( Long scheduleRefId) {
        return this.repository.findAllByReserveStatusAndScheduleRefId(ReservationStatus.COMPLETED, scheduleRefId);
	}

}

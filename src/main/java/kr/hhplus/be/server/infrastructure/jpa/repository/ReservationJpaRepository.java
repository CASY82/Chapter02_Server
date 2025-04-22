package kr.hhplus.be.server.infrastructure.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;


public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
	Reservation findByUserRefId(Long userRefId);
	List<Reservation> findAllByReserveStatusAndScheduleRefId(ReservationStatus reserveStatus, Long scheduleRefId);
}

package kr.hhplus.be.server.infrastructure.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seatreservation.SeatReservation;

public interface SeatReservationJpaRepository extends JpaRepository<SeatReservation, Long> {

	// 시간 안 씀: 모든 PAY 상태 좌석 예약 다 불러옴
	@Query("SELECT sr FROM SeatReservation sr " +
	       "JOIN Reservation r ON sr.reservationRefId = r.id " +
	       "WHERE sr.reserved = true " +
	       "AND r.reserveStatus = :pendingStatus")
	List<SeatReservation> findCancleSeatReservations(ReservationStatus pendingStatus);
}
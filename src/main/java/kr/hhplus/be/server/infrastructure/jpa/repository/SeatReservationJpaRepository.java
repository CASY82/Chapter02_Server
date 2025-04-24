package kr.hhplus.be.server.infrastructure.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seatreservation.SeatReservation;

public interface SeatReservationJpaRepository extends JpaRepository<SeatReservation, Long> {

    @Query("SELECT sr FROM SeatReservation sr " +
            "JOIN Reservation r ON sr.reservationRefId = r.id " +
            "JOIN ReservationItem ri ON r.id = ri.reservation.id " +
            "WHERE ri.scheduleRefId = :scheduleId AND r.reserveStatus != :cancelledStatus")
    List<SeatReservation> findByScheduleIdAndNotCancelled(Long scheduleId, ReservationStatus cancelledStatus);
}
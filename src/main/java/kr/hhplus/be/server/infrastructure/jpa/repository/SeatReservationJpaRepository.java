package kr.hhplus.be.server.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.hhplus.be.server.domain.seatreservation.SeatReservation;

import java.util.List;

public interface SeatReservationJpaRepository extends JpaRepository<SeatReservation, Long> {

    @Query("SELECT sr FROM SeatReservation sr " +
           "JOIN Reservation r ON sr.reservationRefId = r.id " +
           "JOIN ReservationItem ri ON r.id = ri.reservationRefId " +
           "WHERE ri.scheduleRefId = :scheduleId AND r.reservationStatus != 'CANCELLED'")
    List<SeatReservation> findByScheduleIdAndNotCancelled(Long scheduleId);
}
package kr.hhplus.be.server.domain.reservation;

import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findByReservationId(Long reservationId);
    Optional<Reservation> findByReservationIdWithLock(Long reservationId);
    Optional<Reservation> findById(Long id);
    Reservation save(Reservation reservation);
}
package kr.hhplus.be.server.domain.reservation;

import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findByReservationId(Long reservationId);
    Reservation save(Reservation reservation);
}
package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationDomainRepository implements ReservationRepository {

    private final ReservationJpaRepository repository;

    @Override
    public Optional<Reservation> findByReservationId(Long reservationId) {
        return repository.findByReservationId(reservationId);
    }
    
    @Override
    public Optional<Reservation> findByReservationIdWithLock(Long reservationId) {
        return repository.findByReservationIdWithLock(reservationId);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return repository.save(reservation);
    }

	@Override
	public Optional<Reservation> findById(Long id) {
		return repository.findById(id);
	}
}

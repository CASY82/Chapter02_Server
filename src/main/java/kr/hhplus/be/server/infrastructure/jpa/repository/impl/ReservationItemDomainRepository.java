package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import kr.hhplus.be.server.domain.reservationitem.ReservationItem;
import kr.hhplus.be.server.domain.reservationitem.ReservationItemRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.ReservationItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationItemDomainRepository implements ReservationItemRepository {

    private final ReservationItemJpaRepository repository;

    @Override
    public List<ReservationItem> findByReservationRefId(Long reservationRefId) {
        return repository.findByReservation_ReservationId(reservationRefId);
    }

    @Override
    public Optional<ReservationItem> findByIdWithLock(Long id) {
        return repository.findByIdWithLock(id);
    }
    
    @Override
    public ReservationItem save(ReservationItem reservationItem) {
        return repository.save(reservationItem);
    }
}
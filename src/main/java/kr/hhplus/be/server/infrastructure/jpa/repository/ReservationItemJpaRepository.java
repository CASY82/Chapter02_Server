package kr.hhplus.be.server.infrastructure.jpa.repository;

import kr.hhplus.be.server.domain.reservationitem.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationItemJpaRepository extends JpaRepository<ReservationItem, Long> {
    List<ReservationItem> findByReservationRefId(Long reservationRefId);
}
package kr.hhplus.be.server.domain.reservationitem;

import java.util.List;
import java.util.Optional;

public interface ReservationItemRepository {
    List<ReservationItem> findByReservationRefId(Long reservationRefId);
    Optional<ReservationItem> findByIdWithLock(Long id);
	ReservationItem save(ReservationItem reservationItem);
}
package kr.hhplus.be.server.domain.reservationitem;

import java.util.List;

public interface ReservationItemRepository {
    List<ReservationItem> findByReservationRefId(Long reservationRefId);
}
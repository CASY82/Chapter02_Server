package kr.hhplus.be.server.domain.reservationitem;

import java.util.List;

public interface ReservationItemRepository {
	void save(ReservationItem reservationItem);
	void saveAll(List<ReservationItem> reservationItems);
}

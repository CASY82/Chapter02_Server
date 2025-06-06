package kr.hhplus.be.server.domain.reservationitem;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.OptimisticLockException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationItemService {
    private final ReservationItemRepository reservationItemRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public List<ReservationItem> getReservationItems(Long reservationRefId) {
        List<ReservationItem> reservationItems = reservationItemRepository.findByReservationRefId(reservationRefId);
        if (reservationItems.isEmpty()) {
            throw new IllegalArgumentException("No reservation items found for reservationRefId: " + reservationRefId);
        }
        return reservationItems;
    }

    @Transactional
    public ReservationItem save(Long id, Long reservationRefId, Long seatRefId, Long scheduleRefId, int quantity, int unitPrice) {
        Reservation reservation = reservationRepository.findByReservationId(reservationRefId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationRefId));

        ReservationItem item;
        if (id != null) {
            // 기존 항목 업데이트, 비관적 락
            item = reservationItemRepository.findByIdWithLock(id)
                    .orElseThrow(() -> new IllegalArgumentException("Reservation item not found: " + id));
        } else {
            // 신규 항목 생성
            item = new ReservationItem();
        }

        // 필드 설정
        item.setReservation(reservation);
        item.setSeatRefId(seatRefId);
        item.setScheduleRefId(scheduleRefId);
        item.setUnitPrice(unitPrice);
      
        try {
            return reservationItemRepository.save(item);
        } catch (OptimisticLockException e) {
            throw new IllegalStateException("Concurrent update detected for ReservationItem: " + id);
        }
    }
}
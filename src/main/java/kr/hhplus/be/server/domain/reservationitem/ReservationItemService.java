package kr.hhplus.be.server.domain.reservationitem;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationItemService {
    private final ReservationItemRepository reservationItemRepository;

    public List<ReservationItem> getReservationItems(Long reservationRefId) {
        List<ReservationItem> reservationItems = reservationItemRepository.findByReservationRefId(reservationRefId);
        if (reservationItems.isEmpty()) {
            throw new IllegalArgumentException("No reservation items found for reservationRefId: " + reservationRefId);
        }
        return reservationItems;
    }

    public int calculateTotalAmount(Long reservationRefId) {
        List<ReservationItem> reservationItems = getReservationItems(reservationRefId);
        int calculatedTotal = reservationItems.stream()
                .mapToInt(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        // total_amount 무결성 검증
        for (ReservationItem item : reservationItems) {
            if (!item.getTotalAmount().equals(item.getQuantity() * item.getUnitPrice())) {
                throw new IllegalArgumentException("Reservation item total amount does not match calculated amount");
            }
        }
        return calculatedTotal;
    }
}
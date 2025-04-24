package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.reservationitem.ReservationItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationItemService reservationItemService;

    public Reservation getReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found with reservationId: " + reservationId));
        if (!reservation.isReservable()) {
            throw new IllegalArgumentException("Reservation is not reservable");
        }
        if (!ReservationStatus.PAY.equals(reservation.getReserveStatus())) {
            throw new IllegalArgumentException("Reservation is not in PAY status");
        }
        reservationItemService.getReservationItems(reservationId); // ReservationItem 존재 여부 검증
        reservationItemService.calculateTotalAmount(reservationId); // 합계 검증
        return reservation;
    }

    public Reservation completeReservation(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        reservation.reserve();
        return reservationRepository.save(reservation);
    }
}



package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.application.obj.ReservationItemCommand;
import kr.hhplus.be.server.domain.reservationitem.ReservationItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Transactional(readOnly = true)
    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
    }

    @Transactional
    public Reservation completeReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByReservationIdWithLock(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        if (!reservation.isReservable()) {
            throw new IllegalStateException("Reservation already completed: " + reservationId);
        }

        reservation.reserve();
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation createReservationWithItems(Long userRefId, Long seatRefId, Long scheduleRefId, Long orderRefId, List<ReservationItemCommand> items) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(generateUniqueReservationId());
        reservation.setUserRefId(userRefId);
        reservation.setSeatRefId(seatRefId);
        reservation.setScheduleRefId(scheduleRefId);
        reservation.setOrderRefId(orderRefId);
        reservation.setReserveStatus(ReservationStatus.READY);

        for (ReservationItemCommand itemRequest : items) {
            ReservationItem item = new ReservationItem();
            item.setReservation(reservation);
            item.setSeatRefId(seatRefId);
            item.setScheduleRefId(scheduleRefId);
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTotalAmount(itemRequest.getQuantity() * itemRequest.getUnitPrice());
            reservation.getReservationItems().add(item);
        }

        return reservationRepository.save(reservation);
    }

    private Long generateUniqueReservationId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}



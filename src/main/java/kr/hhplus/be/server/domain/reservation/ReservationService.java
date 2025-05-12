package kr.hhplus.be.server.domain.reservation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.reservationitem.ReservationItem;
import lombok.RequiredArgsConstructor;

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
    public Reservation createReservation(Long userRefId, Long scheduleRefId, Long orderRefId, List<ReservationItem> items) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(generateUniqueReservationId());
        reservation.setUserRefId(userRefId);
        reservation.setScheduleRefId(scheduleRefId);
        reservation.setOrderRefId(orderRefId);
        reservation.setReserveStatus(ReservationStatus.READY);
        reservation.setReservationItems(items);

        return reservationRepository.save(reservation);
    }
    
    public void save(Reservation obj) {
    	reservationRepository.save(obj);
    }

    private Long generateUniqueReservationId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}



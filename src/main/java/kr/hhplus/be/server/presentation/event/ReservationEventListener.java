package kr.hhplus.be.server.presentation.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.presentation.event.obj.SeatReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final ReservationService reservationService;

//    @Async
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handleSeatReservedEvent(SeatReservedEvent event) {
//        try {
//            this.reservationService.completeReservation(event.getReservationId());
//        } catch (Exception e) {
//            log.warn("[!] 좌석 예약 알림 발송 실패: {}", e.getMessage());
//        }
//    }
    @KafkaListener(topics = "seat-reserved-topic", groupId = "test-group")
    public void handleSeatReservedEvent(SeatReservedEvent event) {
        try {
            reservationService.completeReservation(event.getReservationId());
            log.info("Successfully completed reservation: {}", event.getReservationId());
        } catch (Exception e) {
            log.warn("[!] 좌석 예약 완료 실패: {}", e.getMessage());
        }
    }
    
}

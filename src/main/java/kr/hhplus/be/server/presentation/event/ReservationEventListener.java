package kr.hhplus.be.server.presentation.event;

import kr.hhplus.be.server.domain.seatreservation.SeatReservationRepository;
import kr.hhplus.be.server.infrastructure.queue.obj.ReservationEvent;
import kr.hhplus.be.server.presentation.event.obj.SeatReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final SeatReservationRepository seatReservationRepository;
    // private final NotifyService notifyService; // 알림 서비스 (필요 시 추가)
    // private final PerformanceService performanceService; // 인기도 업데이트용 (필요 시 추가)

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSeatReservedEvent(SeatReservedEvent event) {
        try {
            // 좌석 예약 알림 발송
            log.info("[이벤트] 좌석 예약 완료 - 예약 ID: {}, 스케줄 ID: {}, 좌석 IDs: {}", 
                    event.getReservationId(), event.getScheduleId(), event.getSeatIds());
            // notifyService.sendReservationNotification(event.getReservationId(), event.getSeatIds());
        } catch (Exception e) {
            log.warn("[!] 좌석 예약 알림 발송 실패: {}", e.getMessage());
        }

        // 좌석 만료 처리
        seatReservationRepository.findByReservationId(event.getReservationId())
                .stream()
                .filter(seat -> seat.getExpiresAt().isBefore(Instant.now()))
                .forEach(seat -> {
                    seat.setReserved(false);
                    seatReservationRepository.save(seat);
                    log.info("[이벤트] 좌석 예약 만료 - 좌석 ID: {}", seat.getSeatRefId());
                });
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationEvent(ReservationEvent event) {
        try {
            // 인기도 업데이트
            log.info("[이벤트] 인기도 업데이트 - 공연 ID: {}, 스케줄 ID: {}, 예약된 좌석: {}/{}", 
                    event.getPerformanceId(), event.getScheduleId(), event.getReservedSeats(), event.getTotalSeats());
            // performanceService.updatePopularity(event.getPerformanceId(), event.getReservedSeats());
        } catch (Exception e) {
            log.warn("[!] 인기도 업데이트 실패: {}", e.getMessage());
        }
    }
}

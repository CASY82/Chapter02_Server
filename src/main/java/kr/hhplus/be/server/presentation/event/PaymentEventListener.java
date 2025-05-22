package kr.hhplus.be.server.presentation.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.presentation.event.obj.PaymentCompleteEvent;

import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
public class PaymentEventListener {
    // private final NotifyService notifyService; // 알림 서비스 (필요 시 추가)

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompletedEvent(PaymentCompleteEvent event) {
        try {
            // 결제 완료 알림 발송
            log.info("[이벤트] 결제 완료 - 결제 ID: {}, 예약 ID: {}, 사용자 ID: {}", 
                    event.getPaymentId(), event.getReservationId(), event.getUserId());
            // notifyService.sendPaymentNotification(event.getPaymentId(), event.getUserId());
        } catch (Exception e) {
            log.warn("[!] 결제 완료 알림 발송 실패: {}", e.getMessage());
        }
    }
}

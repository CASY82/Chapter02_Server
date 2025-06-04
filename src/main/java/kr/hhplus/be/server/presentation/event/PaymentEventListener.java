package kr.hhplus.be.server.presentation.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.presentation.event.obj.PaymentCompleteEvent;

import org.springframework.transaction.event.TransactionPhase;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final OrderService orderService;

//    @Async
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handlePaymentCompletedEvent(PaymentCompleteEvent event) {
//        try {
//        	// 주문 상태 업데이트
//            orderService.updatePaymentRefId(event.getOrderId(), event.getPaymentId());
//        } catch (Exception e) {
//            log.warn("[!] 결제 완료 알림 발송 실패: {}", e.getMessage());
//        }
//    }
    
    @KafkaListener(topics = "payment-complete-topic", groupId = "test-group")
    public void handlePaymentCompleteEvent(PaymentCompleteEvent event) {
        try {
        	// 주문 상태 업데이트
        	 orderService.updatePaymentRefId(event.getOrderId(), event.getPaymentId());
        } catch (Exception e) {
            log.error("[!] 결제 완료 이벤트 처리 실패: {}", e.getMessage());
            throw e;
        }
    }
}

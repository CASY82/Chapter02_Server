package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.infrastructure.kafka.KafkaProducerService;
import kr.hhplus.be.server.presentation.event.obj.PaymentCompleteEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PointService pointService;
    private final ReservationService reservationService;
    private final PaymentRepository paymentRepository;
//    private final ApplicationEventPublisher eventPublisher;
    private final KafkaProducerService kafkaProducerService;


    private Long lastPaymentId; // 임시로 paymentId 저장 (실제로는 DB에서 가져옴)

    @Transactional
    public void processPayment(Long userId, Integer amount, Long reservationId, Long orderId) {
        // 포인트 차감
        Point point = pointService.usePoints(userId, amount);

        // 예약 완료
        reservationService.completeReservation(reservationId);

        // 결제 생성
        Payment payment = new Payment();
        payment.setUserRefId(userId);
        payment.setAmount(amount);
        payment.setPaymentStatus("COMPLETED");
        paymentRepository.save(payment);
        
        // 이벤트 발행
        kafkaProducerService.sendMessage("payment-complete-topic", new PaymentCompleteEvent(
                this.getPaymentId(), reservationId, userId, orderId));
    }

    public Long getPaymentId() {
        return lastPaymentId; // 실제로는 리포지토리에서 조회
    }
}
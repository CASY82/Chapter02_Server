package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PointService pointService;
    private final ReservationService reservationService;
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;

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

        // 주문 상태 업데이트
        orderService.updatePaymentRefId(orderId, payment.getId());

        // paymentId 저장 (이벤트에서 사용)
        this.lastPaymentId = payment.getId();
    }

    public Long getPaymentId() {
        return lastPaymentId; // 실제로는 리포지토리에서 조회
    }
}
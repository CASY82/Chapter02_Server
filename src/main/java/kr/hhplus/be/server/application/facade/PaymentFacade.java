package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservationitem.ReservationItemService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.presentation.api.v1.obj.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final UserService userService;
    private final ReservationService reservationService;
    private final OrderService orderService;
    private final PointService pointService;
    private final PaymentService paymentService;
    private final ReservationItemService reservationItemService;

    @Transactional
    public PaymentResponse payReservation(String userId, Long reservationId) {
        // 사용자 확인
        User user = userService.getUser(userId);

        // 예약 확인 및 소유권 검증 (락 없음)
        Reservation reservation = reservationService.getReservation(reservationId);
        if (!reservation.getUserRefId().equals(user.getId())) {
            throw new IllegalArgumentException("Reservation does not belong to user: " + userId);
        }

        // 주문 확인, 비관적 락
        Order order = orderService.getOrder(reservation.getOrderRefId());
        if (!order.getUserRefId().equals(user.getId())) {
            throw new IllegalArgumentException("Order does not belong to user: " + userId);
        }

        int calculatedTotal = reservationItemService.calculateTotalAmount(reservation.getReservationId());
        if (order.getTotalAmount() != calculatedTotal) {
            throw new IllegalStateException("Order total amount does not match ReservationItem total: " + reservationId);
        }

        // 포인트 사용, 비관적 락
        Point point = pointService.usePoints(user.getId(), order.getTotalAmount());

        // 예약 상태 완료, 비관적 락
        reservationService.completeReservation(reservationId);

        // 결제 기록 생성, 비관적 락
        Payment payment = paymentService.processPayment(user.getId(), order.getTotalAmount());

        // 주문 업데이트
        orderService.updatePaymentRefId(reservation.getOrderRefId(), payment.getId());

        // 응답 생성
        PaymentResponse response = new PaymentResponse();
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setRemainPoint(point.getRemainPoint().longValue());

        return response;
    }

}

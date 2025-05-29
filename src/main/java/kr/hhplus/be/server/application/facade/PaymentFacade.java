package kr.hhplus.be.server.application.facade;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.obj.PaymentCommand;
import kr.hhplus.be.server.application.obj.PaymentResult;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.lock.DistributedLock;
import lombok.RequiredArgsConstructor;

/**
 * 결제 파사드
 * 1. 결제
 */
@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final UserService userService;
    private final ReservationService reservationService;
    private final OrderService orderService;
    private final PointService pointService;
    private final PaymentService paymentService;

    @Transactional
    @DistributedLock(key = "'payLock:' + #command.reservationId", waitTime = 5, leaseTime = 3)
    public PaymentResult pay(PaymentCommand command) {
        // 유저 및 소유권 검증
        User user = userService.getUser(command.getUserId());
        Reservation reservation = reservationService.getReservation(command.getReservationId());
        if (!reservation.getUserRefId().equals(user.getId())) {
            throw new IllegalArgumentException("예약이 사용자에게 속하지 않습니다: " + command.getUserId());
        }

        Order order = orderService.getOrder(reservation.getOrderRefId());
        if (!order.getUserRefId().equals(user.getId())) {
            throw new IllegalArgumentException("주문이 사용자에게 속하지 않습니다: " + command.getUserId());
        }

        // 결제 처리
        paymentService.processPayment(user.getId(), order.getTotalAmount(), reservation.getReservationId(), order.getId());

        PaymentResult response = new PaymentResult();
        response.setPaymentStatus("COMPLETED");
        response.setRemainPoint(pointService.getPointBalance(user.getId()).longValue());
        return response;
    }
}

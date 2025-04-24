package kr.hhplus.be.server.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.presentation.api.v1.obj.PaymentResponse;

@ExtendWith(MockitoExtension.class)
public class PaymentFacadeUnitTest {

    @Mock private UserService userService;
    @Mock private ReservationService reservationService;
    @Mock private OrderService orderService;
    @Mock private PointService pointService;
    @Mock private PaymentService paymentService;

    @InjectMocks
    private PaymentFacade paymentFacade;

    private User user;
    private Reservation reservation;
    private Order order;
    private Point point;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationId(100L);
        reservation.setUserRefId(1L);
        reservation.setOrderRefId(10L);
        reservation.setReserveStatus(ReservationStatus.READY);

        order = new Order();
        order.setId(10L);
        order.setTotalAmount(1000);

        point = new Point();
        point.setRemainPoint(500);

        payment = new Payment();
        payment.setId(20L);
        payment.setPaymentStatus("SUCCESS");
    }

    @Test
    @DisplayName("결제 성공 시 PaymentResponse를 반환한다")
    void 결제_성공() {
        // given
        String userId = "user1";
        Long reservationId = 1L;

        when(userService.getUser(userId)).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(orderService.getOrder(reservation.getOrderRefId())).thenReturn(order);
        when(pointService.usePoint(user.getId(), order.getTotalAmount())).thenReturn(point);
        when(reservationService.completeReservation(reservationId)).thenReturn(reservation);
        when(paymentService.createPayment(anyLong(), anyInt(), anyLong())).thenReturn(payment);
        when(orderService.updatePaymentRefId(order.getId(), payment.getId())).thenReturn(order);

        // when
        PaymentResponse response = paymentFacade.payReservation(userId, reservationId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentStatus()).isEqualTo("SUCCESS");
        assertThat(response.getRemainPoint()).isEqualTo(500L);

        verify(userService).getUser(userId);
        verify(reservationService).getReservation(reservationId);
        verify(orderService).getOrder(reservation.getOrderRefId());
        verify(pointService).usePoint(user.getId(), order.getTotalAmount());
        verify(reservationService).completeReservation(reservationId);
        when(paymentService.createPayment(eq(user.getId()), eq(order.getTotalAmount()), anyLong()))
        .thenReturn(payment);
        verify(orderService).updatePaymentRefId(order.getId(), payment.getId());
    }

    @Test
    @DisplayName("예약이 사용자 소유가 아니면 예외를 던진다")
    void 예약_소유자_불일치_IllegalArgumentException예외() {
        // given
        String userId = "user1";
        Long reservationId = 1L;

        User differentUser = new User();
        differentUser.setId(2L); // 다른 사용자

        reservation.setUserRefId(2L); // 예약은 다른 사용자의 것

        when(userService.getUser(userId)).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            paymentFacade.payReservation(userId, reservationId));

        assertThat(exception.getMessage()).isEqualTo("Reservation does not belong to user");

        verify(userService).getUser(userId);
        verify(reservationService).getReservation(reservationId);
        verifyNoInteractions(orderService, pointService, paymentService);
    }

    @Test
    @DisplayName("포인트 부족 시 예외를 던진다")
    void 포인트_부족_RuntimeException예외() {
        // given
        String userId = "user1";
        Long reservationId = 1L;

        when(userService.getUser(userId)).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(orderService.getOrder(reservation.getOrderRefId())).thenReturn(order);
        when(pointService.usePoint(user.getId(), order.getTotalAmount()))
            .thenThrow(new RuntimeException("Insufficient points"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            paymentFacade.payReservation(userId, reservationId));

        assertThat(exception.getMessage()).isEqualTo("Insufficient points");

        verify(userService).getUser(userId);
        verify(reservationService).getReservation(reservationId);
        verify(orderService).getOrder(reservation.getOrderRefId());
        verify(pointService).usePoint(user.getId(), order.getTotalAmount());
        verifyNoMoreInteractions(reservationService, paymentService, orderService);
    }
}

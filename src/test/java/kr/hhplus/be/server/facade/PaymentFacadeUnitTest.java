package kr.hhplus.be.server.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import kr.hhplus.be.server.application.obj.PaymentCommand;
import kr.hhplus.be.server.application.obj.PaymentResult;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.reservationitem.ReservationItemService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;

@ExtendWith(MockitoExtension.class)
public class PaymentFacadeUnitTest {

    @Mock private UserService userService;
    @Mock private ReservationService reservationService;
    @Mock private OrderService orderService;
    @Mock private PointService pointService;
    @Mock private PaymentService paymentService;
    @Mock private ReservationItemService reservationItemService;

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
        order.setUserRefId(1L);
        order.setTotalAmount(1000);

        point = new Point();
        point.setUserRefId(1L);
        point.setRemainPoint(500);

        payment = new Payment();
        payment.setId(20L);
        payment.setPaymentStatus("SUCCESS");
    }

    @Test
    @DisplayName("결제 성공 시 PaymentResponse를 반환한다")
    void 결제_성공() {
        // given
    	PaymentCommand command = new PaymentCommand();
        String userId = "user1";
        Long reservationId = 100L;
        
        command.setUserId(userId);
        command.setReservationId(reservationId);

        when(userService.getUser(userId)).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(orderService.getOrder(reservation.getOrderRefId())).thenReturn(order);
        when(pointService.usePoints(user.getId(), order.getTotalAmount())).thenReturn(point);
        when(orderService.updatePaymentRefId(order.getId(), payment.getId())).thenReturn(order);

        // when
        PaymentResult result = paymentFacade.pay(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPaymentStatus()).isEqualTo("SUCCESS");
        assertThat(result.getRemainPoint()).isEqualTo(500L);

        verify(userService).getUser(userId);
        verify(reservationService).getReservation(reservationId);
        verify(orderService).getOrder(reservation.getOrderRefId());
        verify(pointService).usePoints(user.getId(), order.getTotalAmount());
        verify(reservationService).completeReservation(reservationId);
        verify(orderService).updatePaymentRefId(order.getId(), payment.getId());
        verifyNoMoreInteractions(userService, reservationService, orderService, pointService, paymentService, reservationItemService);
    }

    @Test
    @DisplayName("예약이 사용자 소유가 아니면 IllegalArgumentException을 던진다")
    void 예약_소유자_불일치_IllegalArgumentException() {
        // given
    	PaymentCommand command = new PaymentCommand();
        String userId = "user1";
        Long reservationId = 100L;
        
        command.setUserId(userId);
        command.setReservationId(reservationId);

        reservation.setUserRefId(2L); // 다른 사용자의 예약

        when(userService.getUser(userId)).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentFacade.pay(command));

        assertThat(exception.getMessage()).isEqualTo("Reservation does not belong to user: " + userId);

        verify(userService).getUser(userId);
        verify(reservationService).getReservation(reservationId);
        verifyNoInteractions(orderService, pointService, paymentService, reservationItemService);
    }

    @Test
    @DisplayName("주문이 사용자 소유가 아니면 IllegalArgumentException을 던진다")
    void 주문_소유자_불일치_IllegalArgumentException() {
        // given
    	PaymentCommand command = new PaymentCommand();
        String userId = "user1";
        Long reservationId = 100L;
        
        command.setUserId(userId);
        command.setReservationId(reservationId);

        order.setUserRefId(2L); // 다른 사용자의 주문

        when(userService.getUser(userId)).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(orderService.getOrder(reservation.getOrderRefId())).thenReturn(order);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                paymentFacade.pay(command));

        assertThat(exception.getMessage()).isEqualTo("Order does not belong to user: " + userId);

        verify(userService).getUser(userId);
        verify(reservationService).getReservation(reservationId);
        verify(orderService).getOrder(reservation.getOrderRefId());
        verifyNoInteractions(pointService, paymentService, reservationItemService);
    }

    @Test
    @DisplayName("Order.totalAmount와 ReservationItem 합계가 다르면 IllegalStateException을 던진다")
    void 금액_불일치_IllegalStateException() {
        // given
    	PaymentCommand command = new PaymentCommand();
        String userId = "user1";
        Long reservationId = 100L;
        
        command.setUserId(userId);
        command.setReservationId(reservationId);

        when(userService.getUser(userId)).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(orderService.getOrder(reservation.getOrderRefId())).thenReturn(order);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                paymentFacade.pay(command));

        assertThat(exception.getMessage()).isEqualTo("Order total amount does not match ReservationItem total: " + reservationId);

        verify(userService).getUser(userId);
        verify(reservationService).getReservation(reservationId);
        verify(orderService).getOrder(reservation.getOrderRefId());
        verifyNoInteractions(pointService, paymentService);
    }

    @Test
    @DisplayName("포인트 부족 시 RuntimeException을 던진다")
    void 포인트_부족_RuntimeException() {
        // given
    	PaymentCommand command = new PaymentCommand();
        String userId = "user1";
        Long reservationId = 100L;
        
        command.setUserId(userId);
        command.setReservationId(reservationId);

        when(userService.getUser(userId)).thenReturn(user);
        when(reservationService.getReservation(reservationId)).thenReturn(reservation);
        when(orderService.getOrder(reservation.getOrderRefId())).thenReturn(order);
        when(pointService.usePoints(user.getId(), order.getTotalAmount()))
                .thenThrow(new RuntimeException("Insufficient points"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                paymentFacade.pay(command));

        assertThat(exception.getMessage()).isEqualTo("Insufficient points");

        verify(userService).getUser(userId);
        verify(reservationService).getReservation(reservationId);
        verify(orderService).getOrder(reservation.getOrderRefId());
        verify(pointService).usePoints(user.getId(), order.getTotalAmount());
        verifyNoInteractions(paymentService);
        verifyNoMoreInteractions(reservationService, orderService);
    }
}

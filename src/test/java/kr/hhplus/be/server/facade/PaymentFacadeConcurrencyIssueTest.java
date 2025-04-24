package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.reservationitem.ReservationItemService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.PaymentJpaRepository;
import kr.hhplus.be.server.presentation.api.v1.obj.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentFacadeConcurrencyIssueTest {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private ReservationItemService reservationItemService;

    @Test
    @Transactional
    void testConcurrentPaymentEnsuresSinglePayment() throws InterruptedException {
        // Given
        String userId = "user1";
        Long reservationId = 100L;
        int numberOfThreads = 10; // 10개의 스레드가 동시에 결제 시도
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 테스트 데이터 설정
        User user = new User();
        user.setId(1L);
        user.setUserId(userId);
        userRepository.save(user);

        Order order = new Order();
        order.setId(10L);
        order.setUserRefId(user.getId());
        order.setTotalAmount(1000);
        orderRepository.save(order);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationId(reservationId);
        reservation.setUserRefId(user.getId());
        reservation.setOrderRefId(order.getId());
        reservation.setReserveStatus(ReservationStatus.READY);
        reservationRepository.save(reservation);

        Point point = new Point();
        point.setUserRefId(user.getId());
        point.setRemainPoint(2000); // 충분한 포인트
        pointRepository.save(point);

        // Mock 설정
        when(reservationItemService.calculateTotalAmount(reservationId)).thenReturn(1000);

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    paymentFacade.payReservation(userId, reservationId);
                } catch (Exception e) {
                    // 동시성 예외는 무시 (단일 성공 확인이 목표)
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드 완료 대기
        latch.await();

        // Then
        // 결제 레코드 확인
        List<Payment> payments = paymentJpaRepository.findAll();
        assertThat(payments).hasSize(1);
        Payment payment = payments.get(0);
        assertThat(payment.getUserRefId()).isEqualTo(user.getId());
        assertThat(payment.getAmount()).isEqualTo(1000);
        assertThat(payment.getPaymentStatus()).isEqualTo("SUCCESS");

        // 예약 상태 확인
        Reservation updatedReservation = reservationRepository.findByReservationId(reservationId).orElseThrow();
        assertThat(updatedReservation.getReserveStatus()).isEqualTo(ReservationStatus.COMPLETED);

        // 주문의 payment_ref_id 확인
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getPaymentRefId()).isEqualTo(payment.getId());

        // 포인트 차감 확인
        Point updatedPoint = pointRepository.findByUserRefId(user.getId()).orElseThrow();
        assertThat(updatedPoint.getRemainPoint()).isEqualTo(1000); // 2000 - 1000

        // 디버깅용 출력
        System.out.println("Number of payments: " + payments.size());
        System.out.println("Reservation status: " + updatedReservation.getReserveStatus());
        System.out.println("Remaining points: " + updatedPoint.getRemainPoint());

        // ExecutorService 종료
        executorService.shutdown();
    }
}
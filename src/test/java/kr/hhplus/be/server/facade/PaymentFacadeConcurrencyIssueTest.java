package kr.hhplus.be.server.facade;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;

@SpringBootTest
public class PaymentFacadeConcurrencyIssueTest {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private SeatService seatService;

    @MockBean
    private PointService pointService;

    @Test
    @Transactional
    void testConcurrentPaymentCausesDuplication() throws InterruptedException {
        // Given
        Long userId = 1L;
        int amount = 1000;
        int numberOfThreads = 10; // 10개의 스레드가 동시에 결제 시도
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // Mock 설정
        Reservation reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setUserRefId(userId);
        reservation.setSeatRefId(1L);
        reservation.setReserveStatus(ReservationStatus.READY);

        Seat seat = new Seat();
        seat.setId(1L);

        // reservationService.getReservationByUser가 동일한 예약 반환
        when(this.reservationService.getReservationByUser(userId)).thenReturn(reservation);
        // reservationService.isReserve가 항상 true 반환
        when(this.reservationService.isReserve(reservation.getReservationId())).thenReturn(true);
        // seatService.getSeat가 유효한 좌석 반환
        when(this.seatService.getSeat(reservation.getSeatRefId())).thenReturn(seat);
        // pointService.usePoint가 호출될 때마다 성공으로 가정
        doNothing().when(pointService).usePoint(userId, amount);

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 동일한 userId로 결제 시도
                    this.paymentFacade.paymentProcess(userId, amount);
                } catch (Exception e) {
                    // 예외는 무시 (동시성 문제 확인이 목적)
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();

        // Then
        // 동일한 userId에 대해 저장된 결제 데이터 조회
        List<Payment> payments = this.paymentRepository.findAll();
        long paymentCount = payments.stream()
                .filter(payment -> payment.getAmount() == amount)
                .count();

        // pointService.usePoint 호출 횟수 확인
        int pointUsageCount = getPointUsageCount();

        // 동시성 문제로 인해 여러 결제가 저장되었거나 포인트가 여러 번 차감되었는지 확인
        assertTrue(paymentCount > 1 || pointUsageCount > 1,
                "Multiple payments or point deductions occurred due to concurrency issue");

        // 결제 수와 포인트 차감 횟수 출력 (디버깅용)
        System.out.println("Number of payments for userId " + userId + ": " + paymentCount);
        System.out.println("Number of point deductions: " + pointUsageCount);

        // ExecutorService 종료
        executorService.shutdown();
    }

    // pointService.usePoint 호출 횟수를 확인하는 헬퍼 메서드
    private int getPointUsageCount() {
        return verify(this.pointService, atLeast(0)).usePoint(anyLong(), anyInt());
    }
}

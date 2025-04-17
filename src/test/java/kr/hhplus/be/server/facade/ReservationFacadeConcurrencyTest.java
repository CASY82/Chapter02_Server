package kr.hhplus.be.server.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.seat.SeatService;

@SpringBootTest
public class ReservationFacadeConcurrencyTest {

    @Autowired
    private ReservationFacade reservationFacade;

    @Mock
    private ReservationService reservationService;
    
    @Autowired
    private ReservationRepository reservationRepository;

    @Mock
    private SeatService seatService;

    @InjectMocks
    private ReservationFacade reservationFacadeUnderTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    void testConcurrentSeatReservation() throws InterruptedException {
        // Given
        Long scheduleId = 1L;
        Long seatId = 1L;
        int numberOfThreads = 10; // 10개의 스레드가 동시에 예약 시도
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // Mock 설정: reservationService.reserveSeat 호출 시 성공으로 가정
        doAnswer(invocation -> {
            Reservation reservation = invocation.getArgument(0);
            // 좌석 예약이 성공한 경우로 가정
            if (reservation.getSeatRefId().equals(seatId)) {
                successCount.incrementAndGet();
            }
            return null;
        }).when(this.reservationService).reserveSeat(any(Reservation.class));

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            final Long userId = (long) (i + 1);
            executorService.submit(() -> {
                try {
                    this.reservationFacadeUnderTest.reserveSeat(scheduleId, seatId, userId);
                } catch (Exception e) {
                    // 예외 발생 시 무시 (실제 구현에서는 예외 처리 필요)
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();

        // Then
        // 동일한 좌석에 대해 단일 예약만 성공해야 함
        assertEquals(1, successCount.get(), "Only one reservation should succeed for the same seat");

        // ExecutorService 종료
        executorService.shutdown();
    }
    
    @Test
    @Transactional
    void testConcurrentSeatReservationCausesDuplication() throws InterruptedException {
        // Given
        Long scheduleId = 1L;
        Long seatId = 1L;
        int numberOfThreads = 10; // 10개의 스레드가 동시에 예약 시도
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            final Long userId = (long) (i + 1); // 각 스레드마다 다른 userId
            executorService.submit(() -> {
                try {
                    // 동일한 scheduleId와 seatId로 예약 시도
                    this.reservationFacade.reserveSeat(scheduleId, seatId, userId);
                } catch (Exception e) {
                    // 예외는 무시 (실제 동시성 문제 확인이 목적)
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();

        // Then
        // 동일한 seatId에 대해 예약된 데이터 조회
        List<Reservation> reservations = reservationRepository.findAllReservedSeat(scheduleId);
        long reservedCountForSeat = reservations.stream()
                .filter(reservation -> reservation.getSeatRefId().equals(seatId))
                .count();

        // 동시성 문제로 인해 동일한 좌석에 대해 2개 이상의 예약이 발생했는지 확인
        assertTrue(reservedCountForSeat > 1, "Multiple reservations were made for the same seat due to concurrency issue");

        // 예약 수 출력 (디버깅용)
        System.out.println("Number of reservations for seatId " + seatId + ": " + reservedCountForSeat);

        // ExecutorService 종료
        executorService.shutdown();
    }
}
package kr.hhplus.be.server.facade;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;

@ExtendWith(MockitoExtension.class)
public class PaymentFacadeUnitTest {

    @Mock private PaymentService paymentService;
    @Mock private PointService pointService;
    @Mock private ReservationService reservationService;
    @Mock private SeatService seatService;

    @InjectMocks
    private PaymentFacade paymentFacade;

//    @Test
//    public void 결제_성공_시_정상_처리() {
//        // given
//        long userId = 1L;
//        int amount = 1000;
//        long seatId = 10L;
//
//        Reservation mockReservation = new Reservation();
//        mockReservation.setSeatRefId(seatId);
//        mockReservation.setReserveStatus(ReservationStatus.READY.name());
//
//        Seat seat = new Seat();
//        
//        when(reservationService.getReservation(userId)).thenReturn(mockReservation);
//        when(seatService.confirmSeat(userId)).thenReturn(true);
//        when(seatService.getSeat(seatId)).thenReturn(seat);
//
//        // when
//        boolean result = paymentFacade.paymentProcess(userId, amount);
//
//        // then
//        assertTrue(result);
//        assertEquals(mockReservation.getReserveStatus(), ReservationStatus.COMPLETE.name());
//        verify(pointService).usePoint(userId, amount);
//        verify(reservationService).reserve(mockReservation);
//        verify(paymentService).save(any(Payment.class));
//    }
//
//    @Test
//    public void 좌석_확인_실패시_false_반환() {
//        // given
//        long userId = 2L;
//        int amount = 2000;
//
//        when(seatService.confirmSeat(userId)).thenReturn(false);
//
//        // when
//        boolean result = paymentFacade.paymentProcess(userId, amount);
//
//        // then
//        assertFalse(result);
//        verify(pointService, never()).usePoint(anyLong(), anyInt());
//        verify(reservationService, never()).reserve(any());
//        verify(paymentService, never()).save(any());
//    }
//
//    @Test
//    public void 포인트_부족_등으로_예외_발생시_false_반환() {
//        // given
//        long userId = 3L;
//        int amount = 9999;
//        long seatId = 30L;
//
//        Reservation mockReservation = new Reservation();
//        mockReservation.setSeatRefId(seatId);
//        mockReservation.setReserveStatus(ReservationStatus.READY.name());
//
//        when(reservationService.getReservation(userId)).thenReturn(mockReservation);
//        when(seatService.confirmSeat(userId)).thenReturn(true);
//        when(seatService.getSeat(seatId)).thenReturn(new Seat());
//        doThrow(new RuntimeException("잔액 부족")).when(pointService).usePoint(userId, amount);
//
//        // when
//        assertThrows(RuntimeException.class, () -> paymentFacade.paymentProcess(userId, amount));
//    }
}

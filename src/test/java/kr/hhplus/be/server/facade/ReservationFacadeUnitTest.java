package kr.hhplus.be.server.facade;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.application.facade.ReserveFacade;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.seat.SeatService;

@ExtendWith(MockitoExtension.class)
class ReservationFacadeUnitTest {

    @Mock private ReservationService reservationService;
    @Mock private SeatService seatService;

    @InjectMocks
    private ReserveFacade reservationFacade;

    @Test
    void 예약_성공_정상흐름() {
        // given
        Long scheduleId = 1L;
        Long seatId = 2L;
        Long userId = 3L;

        // when
        reservationFacade.reservation(scheduleId, seatId, userId);

        // then
        verify(seatService).reserveSeat(seatId);
        verify(reservationService).reserve(argThat(reservation ->
            reservation.getScheduleRefId().equals(scheduleId) &&
            reservation.getSeatRefId().equals(seatId) &&
            reservation.getUserRefId().equals(userId) &&
            reservation.getReserveStatus().equals("READY")
        ));
    }

    // 리팩터 예정
    @Test
    void 좌석_예약_중_예외_발생시_예약저장_실패() {
        // given
        Long scheduleId = 10L;
        Long seatId = 20L;
        Long userId = 30L;

        doThrow(new RuntimeException("좌석 예약 실패")).when(seatService).reserveSeat(seatId);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            reservationFacade.reservation(scheduleId, seatId, userId);
        });

        verify(reservationService, never()).reserve(any(Reservation.class));
    }
}

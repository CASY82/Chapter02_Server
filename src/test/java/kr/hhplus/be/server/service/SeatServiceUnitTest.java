package kr.hhplus.be.server.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.domain.seat.SeatService;

@ExtendWith(MockitoExtension.class)
class SeatServiceUnitTest {

    @Mock
    private SeatRepository repository;

    @InjectMocks
    private SeatService seatService;

    private Seat createTestSeat(Long seatId, boolean reserved) {
        Seat seat = new Seat();
        seat.setId(seatId);
        seat.setSeatId(seatId);
        seat.setReserved(reserved);
        seat.setSeatNumber("A1");
        seat.setSeatRow("A");
        seat.setSeatColumn("1");
        seat.setScheduleRefId(1L);
        seat.setVenueRefId(1L);
        seat.setCreatedAt(Instant.now());
        seat.setUpdatedAt(Instant.now());
        return seat;
    }

    @Test
    void 예약되지_않은_좌석은_정상적으로_예약된다() {
        // given
        Seat seat = createTestSeat(1L, false);
        when(repository.findById(1L)).thenReturn(seat);

        // when
        seatService.reserveSeat(1L);

        // then
        assertTrue(seat.getReserved(), "좌석은 예약 상태여야 합니다.");
        verify(repository).save(seat);
    }

    @Test
    void 이미_예약된_좌석은_예약하려고_하면_예외가_발생한다() {
        // given
        Seat seat = createTestSeat(2L, true);
        when(repository.findById(2L)).thenReturn(seat);

        // when & then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            seatService.reserveSeat(2L);
        });

        assertEquals("이미 예약된 좌석입니다.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void 예약된_좌석은_정상적으로_예약해제된다() {
        // given
        Seat seat = createTestSeat(3L, true);
        when(repository.findById(3L)).thenReturn(seat);

        // when
        seatService.releaseSeat(3L);

        // then
        assertFalse(seat.getReserved(), "좌석은 비예약 상태여야 합니다.");
        verify(repository).save(seat);
    }
}


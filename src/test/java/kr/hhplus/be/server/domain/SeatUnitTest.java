package kr.hhplus.be.server.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.seat.Seat;

public class SeatUnitTest {

    @Test
    public void 좌석_예약가능_테스트() {
        // given
        Seat seat = new Seat(1L, 101L, 10L, null, 1000L, "A1", "A", "1", false);

        // when
        boolean reservable = seat.isReservable();

        // then
        assertTrue(reservable, "예약 가능해야 한다.");
    }

    @Test
    public void 좌석_예약불가_테스트() {
        // given
        Seat seat = new Seat(1L, 101L, 10L, null, 1000L, "A1", "A", "1", true);

        // when
        boolean reservable = seat.isReservable();

        // then
        assertFalse(reservable, "예약 불가능해야 한다.");
    }

    @Test
    public void 좌석_예약처리_테스트() {
        // given
        Seat seat = new Seat(1L, 101L, 10L, null, 1000L, "A1", "A", "1", false);

        // when
        seat.reserve();

        // then
        assertTrue(seat.getReserved(), "예약 상태여야 한다.");
    }

    @Test
    public void 좌석_예약해제_테스트() {
        // given
        Seat seat = new Seat(1L, 101L, 10L, null, 1000L, "A1", "A", "1", true);

        // when
        seat.unmarkReserved();

        // then
        assertFalse(seat.getReserved(), "예약이 해제되어야 한다.");
    }
}

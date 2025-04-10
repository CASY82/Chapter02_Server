package kr.hhplus.be.server.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.seat.Seat;

public class SeatUnitTest {

    @Test
    public void 좌석_예약가능_테스트() {
        // given
        Seat seat = new Seat(1L, 101L, 10L, null, 1000L, "A1", "A", "1", false, Instant.now(), Instant.now());

        // when
        boolean reservable = seat.isReservable();

        // then
        assertTrue(reservable, "예약 가능해야 한다.");
    }

    @Test
    public void 좌석_예약불가_테스트() {
        // given
        Seat seat = new Seat(1L, 101L, 10L, null, 1000L, "A1", "A", "1", true, Instant.now(), Instant.now());

        // when
        boolean reservable = seat.isReservable();

        // then
        assertFalse(reservable, "예약 불가능해야 한다.");
    }

    @Test
    public void 좌석_예약처리_테스트() {
        // given
        Seat seat = new Seat(1L, 101L, 10L, null, 1000L, "A1", "A", "1", false, Instant.now(), Instant.now());

        // when
        seat.reserve();

        // then
        assertTrue(seat.getReserved(), "예약 상태여야 한다.");
        assertNotNull(seat.getUpdatedAt(), "업데이트 시간이 존재해야 한다.");
    }

    @Test
    public void 좌석_예약해제_테스트() {
        // given
        Seat seat = new Seat(1L, 101L, 10L, null, 1000L, "A1", "A", "1", true, Instant.now(), Instant.now());

        // when
        seat.unmarkReserved();

        // then
        assertFalse(seat.getReserved(), "예약이 해제되어야 한다.");
        assertNotNull(seat.getUpdatedAt(), "업데이트 시간이 존재해야 한다.");
    }
}

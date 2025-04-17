package kr.hhplus.be.server.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;

public class ReservationUnitTest {

    private Reservation reservation;

    @BeforeEach
    public void setup() {
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationId(100L);
        reservation.setUserRefId(1L);
        reservation.setOrderRefId(1L);
        reservation.setSeatRefId(1L);
        reservation.setScheduleRefId(1L);
        reservation.setReserveStatus(ReservationStatus.READY);
    }

    @Test
    public void 예약_가능_여부_확인_정상_테스트() {
        // given
        reservation.setReserveStatus(ReservationStatus.READY);

        // when
        boolean isReservable = reservation.isReservable();

        // then
        assertTrue(isReservable, "예약 상태가 COMPLETED가 아니므로 예약 가능해야 한다.");
    }

    @Test
    public void 예약_완료_상태에서는_예약_불가능_테스트() {
        // given
        reservation.setReserveStatus(ReservationStatus.COMPLETED);

        // when
        boolean isReservable = reservation.isReservable();

        // then
        assertFalse(isReservable, "예약 상태가 COMPLETED이므로 예약 불가능해야 한다.");
    }

    @Test
    public void 예약_완료_상태_변경_정상_테스트() {
        // given
        reservation.setReserveStatus(ReservationStatus.READY);

        // when
        reservation.reserve();

        // then
        assertEquals(ReservationStatus.COMPLETED, reservation.getReserveStatus(), "예약 상태가 COMPLETED로 변경되어야 한다.");
    }

    @Test
    public void 예약_취소_상태_변경_정상_테스트() {
        // given
        reservation.setReserveStatus(ReservationStatus.READY);

        // when
        reservation.cancle();

        // then
        assertEquals(ReservationStatus.CANCLE, reservation.getReserveStatus(), "예약 상태가 CANCLE로 변경되어야 한다.");
    }
}
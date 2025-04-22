package kr.hhplus.be.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleRepository;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.user.User;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReservationIntegratuionTest {
	
	@Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private User testUser;
    private Schedule testSchedule;
    private Seat testSeat;

    @BeforeEach
    public void setup() {
        // 테스트용 사용자 생성
        testUser = new User();
        testUser.setUserId("test-user-1");
        testUser.setUsername("TestUser");
        testUser.setPassword("password123");

        // 테스트용 스케줄 생성
        testSchedule = new Schedule();
        testSchedule.setId(1L);
        testSchedule.setPerformanceRefId(1L);
        testSchedule.setVenueRefId(1L);
        scheduleRepository.save(this.testSchedule);

        // 테스트용 좌석 생성
        testSeat = new Seat();
        testSeat.setId(1L);
        testSeat.setScheduleRefId(this.testSchedule.getId());
        seatRepository.save(this.testSeat);
    }

    @Test
    public void 좌석_예약_정상_테스트() {
        // given
        Long userId = this.testUser.getId();
        Long scheduleId = this.testSchedule.getId();
        Long seatId = this.testSeat.getId();

        // 예약 가능한 스케줄 조회
        List<Schedule> schedules = this.scheduleService.getScheduleList(1L, 1L);
        assertFalse(schedules.isEmpty(), "예약 가능한 스케줄이 존재해야 한다.");

        // 예약 가능한 좌석 조회
        List<Seat> seats = this.seatService.getSeatList(scheduleId);
        assertFalse(seats.isEmpty(), "예약 가능한 좌석이 존재해야 한다.");

        // when
        this.reservationFacade.reserveSeat(scheduleId, seatId, userId);

        // then
        Reservation reservation = this.reservationService.getReservationByUser(userId);
        assertNotNull(reservation, "예약이 생성되어야 한다.");
        assertEquals(userId, reservation.getUserRefId(), "예약의 userRefId가 일치해야 한다.");
        assertEquals(scheduleId, reservation.getScheduleRefId(), "예약의 scheduleRefId가 일치해야 한다.");
        assertEquals(seatId, reservation.getSeatRefId(), "예약의 seatRefId가 일치해야 한다.");
        assertEquals(ReservationStatus.COMPLETED, reservation.getReserveStatus(), "예약 상태는 COMPLETED이어야 한다.");
    }

    @Test
    public void 이미_예약된_좌석_예약_실패_테스트() {
        // given
        Long userId = this.testUser.getId();
        Long scheduleId = this.testSchedule.getId();
        Long seatId = this.testSeat.getId();

        // 첫 번째 예약
        this.reservationFacade.reserveSeat(scheduleId, seatId, userId);

        // 동일한 좌석으로 두 번째 예약 시도
        Reservation duplicateReservation = new Reservation();
        duplicateReservation.setReserveStatus(ReservationStatus.READY);
        duplicateReservation.setScheduleRefId(scheduleId);
        duplicateReservation.setSeatRefId(seatId);
        duplicateReservation.setUserRefId(userId + 1); // 다른 사용자

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            this.reservationService.reserveSeat(duplicateReservation);
        }, "이미 예약된 좌석은 예약할 수 없어야 한다.");
    }
}

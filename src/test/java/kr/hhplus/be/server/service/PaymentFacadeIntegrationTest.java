//package kr.hhplus.be.server.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.time.Instant;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import kr.hhplus.be.server.application.facade.PaymentFacade;
//import kr.hhplus.be.server.domain.payment.Payment;
//import kr.hhplus.be.server.domain.payment.PaymentRepository;
//import kr.hhplus.be.server.domain.point.Point;
//import kr.hhplus.be.server.domain.point.PointRepository;
//import kr.hhplus.be.server.domain.point.PointService;
//import kr.hhplus.be.server.domain.reservation.Reservation;
//import kr.hhplus.be.server.domain.reservation.ReservationRepository;
//import kr.hhplus.be.server.domain.reservation.ReservationService;
//import kr.hhplus.be.server.domain.reservation.ReservationStatus;
//import kr.hhplus.be.server.domain.seat.Seat;
//import kr.hhplus.be.server.domain.seat.SeatRepository;
//import kr.hhplus.be.server.domain.token.TokenService;
//import kr.hhplus.be.server.domain.user.User;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//public class PaymentFacadeIntegrationTest {
//
//    @Autowired
//    private PaymentFacade paymentFacade;
//
//    @Autowired
//    private PointService pointService;
//    
//    @Autowired
//    private PointRepository pointRepository;
//
//    @Autowired
//    private ReservationService reservationService;
//
//    @Autowired
//    private TokenService tokenService;
//
//    @Autowired
//    private ReservationRepository reservationRepository;
//
//    @Autowired
//    private SeatRepository seatRepository;
//
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    private User testUser;
//    private Seat testSeat;
//    private Reservation testReservation;
//
//    @BeforeEach
//    public void setup() {
//        // 테스트용 사용자 생성
//        testUser = new User();
//        testUser.setUserId("test-user-1");
//        testUser.setUsername("TestUser");
//        testUser.setPassword("password123");
//
//        // 테스트용 좌석 생성
//        testSeat = new Seat();
//        testSeat.setId(1L);
//        testSeat.setScheduleRefId(1L);
//        seatRepository.save(testSeat);
//
//        // 테스트용 예약 생성
//        testReservation = new Reservation();
//        testReservation.setReservationId(1L);
//        testReservation.setUserRefId(testUser.getId());
//        testReservation.setOrderRefId(1L);
//        testReservation.setSeatRefId(testSeat.getId());
//        testReservation.setScheduleRefId(1L);
//        testReservation.setReserveStatus(ReservationStatus.READY);
//        reservationRepository.save(testReservation);
//
//        // 테스트용 포인트 생성
//        Point point = new Point();
//        point.setUserRefId(testUser.getId());
//        point.setRemainPoint(0);
//        pointRepository.save(point);
//    }
//
//    @Test
//    public void 포인트_충전_조회_결제_정상_테스트() {
//        // given
//        Long userId = this.testUser.getId();
//        int chargeAmount = 10000;
//        int paymentAmount = 5000;
//
//        // 토큰 발급
//        String jwt = this.tokenService.issueToken(userId, "queue-position-1");
//        assertTrue(this.tokenService.validateTokenValue(jwt), "토큰이 유효해야 한다.");
//
//        // 포인트 충전
//        this.pointService.chargePoint(userId, chargeAmount);
//        Integer balance = this.pointService.getPoint(userId);
//        assertEquals(chargeAmount, balance, "충전 후 잔액이 일치해야 한다.");
//
//        // when
//        boolean paymentResult = this.paymentFacade.paymentProcess(userId, paymentAmount);
//
//        // then
//        assertTrue(paymentResult, "결제가 성공해야 한다.");
//
//        // 포인트 잔액 확인
//        Integer finalBalance = this.pointService.getPoint(userId);
//        assertEquals(chargeAmount - paymentAmount, finalBalance, "결제 후 포인트가 올바르게 차감되어야 한다.");
//
//        // 예약 상태 확인
//        Reservation reservation = this.reservationService.getReservationByUser(userId);
//        assertEquals(ReservationStatus.COMPLETED, reservation.getReserveStatus(), "예약 상태가 COMPLETED로 변경되어야 한다.");
//
//        // 결제 내역 확인
//        Payment payment = this.paymentRepository.findByUserRefId(userId);
//        assertNotNull(payment, "결제 내역이 저장되어야 한다.");
//        assertEquals(paymentAmount, payment.getAmount(), "결제 금액이 일치해야 한다.");
//        assertEquals(userId, payment.getUserRefId(), "결제의 userRefId가 일치해야 한다.");
//        assertTrue(payment.getPaymentDate().isBefore(Instant.now().plusSeconds(1)), "결제 시간이 현재 시간 근처여야 한다.");
//    }
//
//    @Test
//    public void 예약_불가능_상태에서_결제_실패_테스트() {
//        // given
//        Long userId = this.testUser.getId();
//        int chargeAmount = 10000;
//        int paymentAmount = 5000;
//
//        // 포인트 충전
//        this.pointService.chargePoint(userId, chargeAmount);
//
//        // 예약 상태를 CANCLE로 변경
//        this.testReservation.cancle();
//        this.reservationRepository.save(this.testReservation);
//
//        // when
//        boolean paymentResult = this.paymentFacade.paymentProcess(userId, paymentAmount);
//
//        // then
//        assertFalse(paymentResult, "예약이 불가능한 상태에서는 결제가 실패해야 한다.");
//
//        // 포인트 잔액 확인 (변경 없음)
//        Integer balance = this.pointService.getPoint(userId);
//        assertEquals(chargeAmount, balance, "결제 실패 시 포인트가 차감되지 않아야 한다.");
//
//        // 예약 상태 확인
//        Reservation reservation = this.reservationService.getReservationByUser(userId);
//        assertEquals(ReservationStatus.CANCLE, reservation.getReserveStatus(), "예약 상태는 CANCLE로 유지되어야 한다.");
//
//        // 결제 내역 확인
//        Payment payment = this.paymentRepository.findByUserRefId(userId);
//        assertNull(payment, "결제 내역이 생성되지 않아야 한다.");
//    }
//}
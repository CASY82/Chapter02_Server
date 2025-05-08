package kr.hhplus.be.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.application.obj.PaymentCommand;
import kr.hhplus.be.server.application.obj.PaymentResult;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.reservationitem.ReservationItem;
import kr.hhplus.be.server.domain.reservationitem.ReservationItemRepository;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentFacadeIntegrationTest {

    @Autowired
    private PaymentFacade paymentFacade;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PointRepository pointRepository;
    
    @Autowired 
    private ReservationRepository reservationRepository;
    
    @Autowired 
    private OrderRepository orderRepository;
    
    @Autowired 
    private ReservationItemRepository reservationItemRepository;
    
    @Autowired 
    private SeatRepository seatRepository;
    
    @Autowired 
    private PaymentRepository paymentRepository;

    private User user;
    private Reservation reservation;
    private Order order;
    private Seat seat;
    private Long reservationId;

    @BeforeEach
    void setUp() {
        // 사용자
        user = new User();
        user.setUserId("test-user");
        user.setUsername("Tester");
        user.setPassword("pw123");
        userRepository.save(user);

        // 포인트
        Point point = new Point();
        point.setUserRefId(user.getId());
        point.setRemainPoint(10_000);
        pointRepository.save(point);

        // 좌석
        seat = new Seat();
        seat.setSeatId(101L); // 유니크
        seat.setVenueRefId(1L);
        seat.setSeatNumber("A1");
        seat.setSeatRow("A");
        seat.setSeatColumn("1");
        seat.setVersion(0);
        seatRepository.save(seat);

        // 주문
        order = new Order();
        order.setUserRefId(user.getId());
        order.setTotalAmount(5000);
        orderRepository.save(order);

        // 예약
        reservation = new Reservation();
        reservation.setUserRefId(user.getId());
        reservation.setOrderRefId(order.getId());
        reservation.setScheduleRefId(10L); // 테스트용 임의의 스케줄
        reservation.setReserveStatus(ReservationStatus.READY);
        reservationRepository.save(reservation);
        reservationId = reservation.getReservationId();

        // 예약 아이템
        ReservationItem item = new ReservationItem();
        item.setReservation(reservation);
        item.setSeatRefId(seat.getSeatId());
        item.setScheduleRefId(10L);
        item.setUnitPrice(5000);
        reservationItemRepository.save(item);
        
       
    }

    @Test
    void 결제_정상_동작_테스트() {
        // when
    	PaymentCommand command = new PaymentCommand();
        command.setReservationId(reservationId);
        command.setUserId(user.getUserId());
         
        PaymentResult result = paymentFacade.pay(command);

        // then
        assertEquals("PAID", result.getPaymentStatus());
        assertEquals(5000L, result.getRemainPoint());

        Reservation updatedReservation = reservationRepository.findById(reservationId).orElseThrow();
        assertEquals(ReservationStatus.COMPLETED, updatedReservation.getReserveStatus());

        List<Payment> payments = paymentRepository.findAll();
        assertEquals(1, payments.size());
        assertEquals(user.getId(), payments.get(0).getUserRefId());
        assertEquals(5000, payments.get(0).getAmount());
    }

    @Test
    void 잘못된_사용자_예외() {
        User hacker = new User();
        hacker.setUserId("hacker");
        hacker.setUsername("h4ck3r");
        hacker.setPassword("1234");
        userRepository.save(hacker);
        
        PaymentCommand command = new PaymentCommand();
        command.setReservationId(reservationId);
        command.setUserId(hacker.getUserId());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
            paymentFacade.pay(command)
        );
        assertTrue(e.getMessage().contains("Reservation does not belong"));
    }

    @Test
    void 포인트_부족_예외() {
    	PaymentCommand command = new PaymentCommand();
        command.setReservationId(reservationId);
        command.setUserId(user.getUserId());
    	
        pointRepository.findByUserRefId(user.getId()).ifPresent(p -> {
            p.setRemainPoint(100);
            pointRepository.save(p);
        });
        
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
            paymentFacade.pay(command)
        );
        assertTrue(e.getMessage().contains("Insufficient points"));
    }

    @Test
    void 총액_불일치_예외() {
    	PaymentCommand command = new PaymentCommand();
        command.setReservationId(reservationId);
        command.setUserId(user.getUserId());
    	
        order.setTotalAmount(9999);
        orderRepository.save(order);

        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
            paymentFacade.pay(command)
        );
        assertTrue(e.getMessage().contains("does not match ReservationItem total"));
    }
}

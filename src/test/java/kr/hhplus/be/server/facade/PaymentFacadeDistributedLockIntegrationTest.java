package kr.hhplus.be.server.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.application.obj.PaymentCommand;
import kr.hhplus.be.server.application.obj.PaymentResult;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;

@SpringBootTest
@Testcontainers
class PaymentFacadeDistributedLockIntegrationTest {

    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("mydb")
            .withUsername("myuser")
            .withPassword("mypassword");

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>("redis:7.0")
            .withExposedPorts(6379);

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PointService pointService;

    private User testUser;
    private Order testOrder;
    private Reservation testReservation;
    private Point testPoint;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @BeforeAll
    static void startContainers() {
        mysql.start();
        redis.start();
    }

    @AfterAll
    static void stopContainers() {
        mysql.stop();
        redis.stop();
    }

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        testUser = new User();
        testUser.setUserId("testUser");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setName("Test User");
        userService.save(testUser);

        testOrder = new Order();
        testOrder.setOrderId(1L);
        testOrder.setUserRefId(testUser.getId());
        testOrder.setOrderDate(Instant.now());
        testOrder.setTotalAmount(100);
        testOrder.setOrderStatus("PENDING");
        orderService.save(testOrder);

        testReservation = new Reservation();
        testReservation.setReservationId(1L);
        testReservation.setUserRefId(testUser.getId());
        testReservation.setOrderRefId(testOrder.getId());
        testReservation.setScheduleRefId(1L);
        testReservation.setReserveStatus(ReservationStatus.READY);
        reservationService.save(testReservation);

        testPoint = new Point();
        testPoint.setUserRefId(testUser.getId());
        testPoint.setRemainPoint(1000);
        pointService.save(testPoint);
    }

    @Test
    void testPaySuccess() {
        // Given
        PaymentCommand command = new PaymentCommand();
        command.setUserId(testUser.getUserId());
        command.setReservationId(testReservation.getReservationId());

        // When
        PaymentResult result = paymentFacade.pay(command);

        // Then
        assertNotNull(result);
        assertEquals("SUCCESS", result.getPaymentStatus());
        assertEquals(900L, result.getRemainPoint());

        Reservation reservation = reservationService.getReservation(testReservation.getReservationId());
        assertEquals(ReservationStatus.COMPLETED, reservation.getReserveStatus());

        Integer remainPoint = pointService.getPointBalance(testUser.getId());
        assertEquals(900, remainPoint);
    }

    @Test
    void testPayFailsWithInvalidUser() {
        // Given
        User invalidUser = new User();
        invalidUser.setUserId("invalidUser");
        invalidUser.setUsername("invalidUsername");
        invalidUser.setPassword("invalidPassword");
        invalidUser.setName("Invalid User");
        userService.save(invalidUser);

        PaymentCommand command = new PaymentCommand();
        command.setUserId(invalidUser.getUserId());
        command.setReservationId(testReservation.getReservationId());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paymentFacade.pay(command));
        assertEquals("Reservation does not belong to user: " + invalidUser.getUserId(), exception.getMessage());
    }

    @Test
    void testConcurrentPay() throws InterruptedException {
        // Given
        PaymentCommand command = new PaymentCommand();
        command.setUserId(testUser.getUserId());
        command.setReservationId(testReservation.getReservationId());

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int taskCount = 5;

        // When
        for (int i = 0; i < taskCount; i++) {
            executorService.submit(() -> {
                try {
                    paymentFacade.pay(command);
                } catch (Exception e) {
                    // 락 획득 실패 시 IllegalStateException 예상
                    assertTrue(e instanceof IllegalStateException);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // Then
        Integer remainPoint = pointService.getPointBalance(testUser.getId());
        assertEquals(900, remainPoint); // 100 감소, 한 번만 실행됨

        Reservation reservation = reservationService.getReservation(testReservation.getReservationId());
        assertEquals(ReservationStatus.COMPLETED, reservation.getReserveStatus());
    }
}

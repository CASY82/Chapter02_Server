package kr.hhplus.be.server.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
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

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.application.obj.ReserveCommand;
import kr.hhplus.be.server.application.obj.ReserveResult;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seatreservation.SeatReservation;
import kr.hhplus.be.server.domain.seatreservation.SeatReservationRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;

@SpringBootTest
@Testcontainers
class ReservationFacadeDistributedLockIntegrationTest {

    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("mydb")
            .withUsername("myuser")
            .withPassword("mypassword");

    @Container
    private static final GenericContainer<?> redis = new GenericContainer<>("redis:7.0")
            .withExposedPorts(6379);

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatReservationRepository seatReservationRepository;

    @Autowired
    private ReservationService reservationService;

    private User testUser;
    private Schedule testSchedule;
    private Seat testSeat1;
    private Seat testSeat2;
    private Long testOrderId;

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

        testSchedule = new Schedule();
        testSchedule.setScheduleId(1L);
        testSchedule.setVenueRefId(1L);
        scheduleService.save(testSchedule);

        testSeat1 = new Seat();
        testSeat1.setSeatId(1L);
        testSeat1.setVenueRefId(1L);
        testSeat2 = new Seat();
        testSeat2.setSeatId(2L);
        testSeat2.setVenueRefId(1L);
        seatService.save(testSeat1);
        seatService.save(testSeat2);

        testOrderId = 1L;
    }

    @Test
    void testReserveSuccess() {
        // Given
        ReserveCommand command = new ReserveCommand();
        command.setUserId(testUser.getUserId());
        command.setScheduleId(testSchedule.getScheduleId());
        command.setOrderId(testOrderId);
        command.setSeatId(Arrays.asList(1L, 2L));
        command.setPrice(100);

        // When
        ReserveResult result = reservationFacade.reserve(command);

        // Then
        assertNotNull(result);
        assertEquals(ReservationStatus.COMPLETED.name(), result.getStatus());
        assertNotNull(result.getReservationId());
        assertEquals(Arrays.asList(1L, 2L), result.getSeatIds());

        Reservation reservation = reservationService.getReservation(result.getReservationId());
        assertEquals(ReservationStatus.COMPLETED, reservation.getReserveStatus());
        assertEquals(testUser.getId(), reservation.getUserRefId());
        assertEquals(testOrderId, reservation.getOrderRefId());
        assertEquals(testSchedule.getScheduleId(), reservation.getScheduleRefId());

        List<SeatReservation> seatReservations = seatReservationRepository.findByScheduleIdAndNotCancelled();
        assertEquals(2, seatReservations.size());
        assertTrue(seatReservations.stream().allMatch(SeatReservation::getReserved));
        assertTrue(seatReservations.stream().map(SeatReservation::getSeatRefId).toList().containsAll(Arrays.asList(1L, 2L)));
    }

    @Test
    void testReserveFailsWithInvalidSchedule() {
        // Given
        ReserveCommand command = new ReserveCommand();
        command.setUserId(testUser.getUserId());
        command.setScheduleId(999L); // 존재하지 않는 스케줄
        command.setOrderId(testOrderId);
        command.setSeatId(Arrays.asList(1L, 2L));
        command.setPrice(100);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationFacade.reserve(command));
        assertEquals("스케줄을 찾을 수 없습니다", exception.getMessage());
    }

    @Test
    void testReserveFailsWithReservedSeat() {
        // Given
        SeatReservation reservedSeat = new SeatReservation();
        reservedSeat.setSeatRefId(1L);
        reservedSeat.setReservationRefId(1L);
        reservedSeat.setReserved(true);
        reservedSeat.setReservedAt(Instant.now());
        seatReservationRepository.save(reservedSeat);

        ReserveCommand command = new ReserveCommand();
        command.setUserId(testUser.getUserId());
        command.setScheduleId(testSchedule.getScheduleId());
        command.setOrderId(testOrderId);
        command.setSeatId(Arrays.asList(1L, 2L));
        command.setPrice(100);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> reservationFacade.reserve(command));
        assertEquals("선택한 좌석 중 일부가 이미 예약되었습니다", exception.getMessage());
    }

    @Test
    void testReserveFailsWithInvalidUser() {
        // Given
        ReserveCommand command = new ReserveCommand();
        command.setUserId("invalidUser"); // 존재하지 않는 사용자
        command.setScheduleId(testSchedule.getScheduleId());
        command.setOrderId(testOrderId);
        command.setSeatId(Arrays.asList(1L, 2L));
        command.setPrice(100);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationFacade.reserve(command));
        assertTrue(exception.getMessage().contains("User not found") || exception.getMessage().contains("invalidUser"));
    }

    @Test
    void testConcurrentReserve() throws InterruptedException {
        // Given
        ReserveCommand command = new ReserveCommand();
        command.setUserId(testUser.getUserId());
        command.setScheduleId(testSchedule.getScheduleId());
        command.setOrderId(testOrderId);
        command.setSeatId(Arrays.asList(1L, 2L));
        command.setPrice(100);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int taskCount = 5;

        // When
        for (int i = 0; i < taskCount; i++) {
            executorService.submit(() -> {
                try {
                    reservationFacade.reserve(command);
                } catch (Exception e) {
                    assertTrue(e instanceof IllegalStateException || e instanceof IllegalArgumentException);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // Then
        List<SeatReservation> seatReservations = seatReservationRepository.findByScheduleIdAndNotCancelled();
        assertEquals(2, seatReservations.size()); // 한 번만 예약 성공
        assertTrue(seatReservations.stream().allMatch(SeatReservation::getReserved));
        assertTrue(seatReservations.stream().map(SeatReservation::getSeatRefId).toList().containsAll(Arrays.asList(1L, 2L)));

        Reservation reservation = reservationService.getReservation(seatReservations.get(0).getReservationRefId());
        assertEquals(ReservationStatus.COMPLETED, reservation.getReserveStatus());
    }
}

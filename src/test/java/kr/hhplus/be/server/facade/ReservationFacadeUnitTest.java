package kr.hhplus.be.server.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.application.obj.ReservationCheckCommand;
import kr.hhplus.be.server.application.obj.ReservationCheckResult;
import kr.hhplus.be.server.domain.performance.Performance;
import kr.hhplus.be.server.domain.performance.PerformanceService;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleService;

@ExtendWith(MockitoExtension.class)
public class ReservationFacadeUnitTest {

    @Mock private PerformanceService performanceService;
    @Mock private ScheduleService scheduleService;

    @InjectMocks
    private ReservationFacade reservationFacade;

    private Performance performance;
    private List<Schedule> schedules;

    @BeforeEach
    void setUp() {
        // Performance 객체 초기화
        performance = new Performance();
        performance.setId(1L);
        performance.setPerformanceId(1001L);
        performance.setVenueRefId(201L);
        performance.setPerformanceName("Test Performance");
        performance.setDescription("A test performance description");
        performance.setStartDate(Instant.now().plusSeconds(86400)); // 1일 후
        performance.setEndDate(Instant.now().plusSeconds(172800)); // 2일 후

        // Schedule 객체 초기화
        Schedule schedule1 = new Schedule();
        schedule1.setId(1L);
        schedule1.setScheduleId(101L);
        schedule1.setPerformanceRefId(1L);
        schedule1.setVenueRefId(201L);
        schedule1.setScheduleDateTime(Instant.now().plusSeconds(86400)); // 1일 후

        Schedule schedule2 = new Schedule();
        schedule2.setId(2L);
        schedule2.setScheduleId(102L);
        schedule2.setPerformanceRefId(1L);
        schedule2.setVenueRefId(201L);
        schedule2.setScheduleDateTime(Instant.now().plusSeconds(172800)); // 2일 후

        schedules = Arrays.asList(schedule1, schedule2);
    }

    @Test
    @DisplayName("공연이 존재하면 예약 가능 일정을 반환한다")
    void 예약_가능_일정_조회_성공() {
        // given
    	ReservationCheckCommand command = new ReservationCheckCommand();
        Long performanceId = 1L;
        
        command.setPerformanceId(performanceId);

        when(performanceService.getPerformance(performanceId)).thenReturn(performance);
        when(scheduleService.getAvailableSchedules(performanceId)).thenReturn(schedules);

        // when
        ReservationCheckResult result = reservationFacade.getAvailableSchedules(command);

        // then
        assertThat(result.getScheduleList()).isNotNull();
        assertThat(result.getScheduleList()).hasSize(2);
        assertThat(result.getScheduleList().get(0).getPerformanceRefId()).isEqualTo(performanceId);
        assertThat(result.getScheduleList().get(0).getScheduleId()).isEqualTo(101L);
        assertThat(result.getScheduleList().get(0).getVenueRefId()).isEqualTo(201L);
        assertThat(result.getScheduleList().get(1).getPerformanceRefId()).isEqualTo(performanceId);
        assertThat(result.getScheduleList()).isEqualTo(102L);
        assertThat(result.getScheduleList()).isEqualTo(201L);

        verify(performanceService).getPerformance(performanceId);
        verify(scheduleService).getAvailableSchedules(performanceId);
    }

    @Test
    @DisplayName("공연이 존재하지 않으면 예외를 던진다")
    void 공연_없음_예외() {
        // given
    	ReservationCheckCommand command = new ReservationCheckCommand();
        Long performanceId = 999L;
        
        command.setPerformanceId(performanceId);

        when(performanceService.getPerformance(performanceId))
            .thenThrow(new RuntimeException("Performance not found"));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            reservationFacade.getAvailableSchedules(command));

        assertThat(exception.getMessage()).isEqualTo("Performance not found");

        verify(performanceService).getPerformance(performanceId);
        verify(scheduleService, never()).getAvailableSchedules(performanceId);
    }

    @Test
    @DisplayName("예약 가능 일정이 없으면 빈 리스트를 반환한다")
    void 예약_가능_일정_없음() {
        // given
    	ReservationCheckCommand command = new ReservationCheckCommand();
        Long performanceId = 1L;
        
        command.setPerformanceId(performanceId);

        when(performanceService.getPerformance(performanceId)).thenReturn(performance);
        when(scheduleService.getAvailableSchedules(performanceId)).thenReturn(Arrays.asList());

        // when
        ReservationCheckResult result = reservationFacade.getAvailableSchedules(command);

        // then
        assertThat(result.getScheduleList()).isNotNull();
        assertThat(result.getScheduleList()).isEmpty();

        verify(performanceService).getPerformance(performanceId);
        verify(scheduleService).getAvailableSchedules(performanceId);
    }
}

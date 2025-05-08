package kr.hhplus.be.server.presentation;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.application.obj.ReservationCheckCommand;
import kr.hhplus.be.server.application.obj.ReservationCheckResult;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.presentation.api.v1.reserve.ReservationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservationControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private ReservationFacade reservationFacade;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(reservationController)
                .build();
    }

    @Test
    void 좌석_조회_정상_케이스() throws Exception {
    	ReservationCheckCommand command = new ReservationCheckCommand();
    	command.setScheduleId(1L);
    	
    	ReservationCheckResult result = new ReservationCheckResult();
    	result.setSeatIds(List.of(101L, 102L));
    	
        when(reservationFacade.getAvailableSeatIds(command))
                .thenReturn(result);

        mockMvc.perform(get("/reservations/available/seat")
                        .param("scheduleId", "1")
                        .param("userId", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatIds[0]").value(101))
                .andExpect(jsonPath("$.seatIds[1]").value(102));

        verify(reservationFacade, times(1)).getAvailableSeatIds(command);
    }

    @Test
    void 좌석_조회_비정상_스케줄ID() throws Exception {
        mockMvc.perform(get("/reservations/available/seat")
                        .param("scheduleId", "-1")
                        .param("userId", "1001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 스케줄_조회_정상_케이스() throws Exception {
    	ReservationCheckCommand command = new ReservationCheckCommand();
    	command.setScheduleId(1L);
    	
    	ReservationCheckResult result = new ReservationCheckResult();
    	
        Schedule schedule1 = new Schedule(
                1L,
                201L,
                1L,
                1L,
                Instant.parse("2025-04-20T00:00:00Z")
        );
        Schedule schedule2 = new Schedule(
                2L,
                202L,
                1L,
                1L,
                Instant.parse("2025-04-21T00:00:00Z")
        );
        
        result.setScheduleList(List.of(schedule1, schedule2));

        when(reservationFacade.getAvailableSchedules(command))
                .thenReturn(result);

        mockMvc.perform(get("/reservations/available/schedule")
                        .param("performanceId", "1")
                        .param("userId", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schedules[0].scheduleId").value(201))
                .andExpect(jsonPath("$.schedules[0].scheduleDateTime").value("2025-04-20T00:00:00Z"))
                .andExpect(jsonPath("$.schedules[1].scheduleId").value(202))
                .andExpect(jsonPath("$.schedules[1].scheduleDateTime").value("2025-04-21T00:00:00Z"));

        verify(reservationFacade, times(1)).getAvailableSchedules(command);
    }
    
    @Test
    void 스케줄_조회_비정상_케이스() throws Exception {
        mockMvc.perform(get("/reservations/available/schedule")
                        .param("performanceId", "-1")
                        .param("userId", "1001"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/reservations/available/schedule")
                        .param("performanceId", "1")
                        .param("userId", "-2"))
                .andExpect(status().isBadRequest());
    }
}

package kr.hhplus.be.server.presentation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import kr.hhplus.be.server.application.facade.ReservationFacade;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.presentation.api.v1.reserve.ReservationController;

@Deprecated
@ExtendWith(MockitoExtension.class)
class ReservationControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private SeatService seatService;

    @Mock
    private ScheduleService scheduleService;

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

//    @Test
//    void 좌석_조회_정상_케이스() throws Exception {
//        Seat seat1 = new Seat(
//                1L,         // id
//                101L,       // seatId
//                10L,        // venueRefId
//                1001L,      // userRefId
//                201L,       // scheduleRefId
//                "A1",       // seatNumber
//                "A",        // seatRow
//                "1",        // seatColumn
//                false      // reserved
//        );
//
//        Seat seat2 = new Seat(
//                2L, 102L, 10L, 1002L, 201L, "A2", "A", "2", false
//        );
//        when(seatService.getAvailableSeatList(1L)).thenReturn(List.of(
//                seat1, seat2));
//
//        mockMvc.perform(get("/reservations/available/seat")
//                        .param("scheduleRefId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.seatIds[0]").value(101))
//                .andExpect(jsonPath("$.seatIds[1]").value(102));
//
//        verify(seatService, times(1)).getAvailableSeatList(1L);
//    }

    @Test
    void 좌석_조회_비정상_스케줄ID() throws Exception {
        mockMvc.perform(get("/reservations/available/seat")
                        .param("scheduleRefId", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 스케줄_조회_정상_케이스() throws Exception {
    	 Schedule schedule1 = new Schedule(
    	            1L,        // id
    	            201L,      // scheduleId
    	            301L,      // performanceRefId
    	            401L,      // venueRefId
    	            "2025-04-20" // date
    	    );

    	    Schedule schedule2 = new Schedule(
    	            2L, 202L, 301L, 401L, "2025-04-21"
    	    );
    	
        when(scheduleService.getScheduleList(1L, 2L)).thenReturn(List.of(
        		schedule1, schedule2
        ));

        mockMvc.perform(get("/reservations/available/schedule")
                        .param("performanceId", "1")
                        .param("venueId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dateList[0]").value("2025-04-20"))
                .andExpect(jsonPath("$.dateList[1]").value("2025-04-21"));

        verify(scheduleService, times(1)).getScheduleList(1L, 2L);
    }

    @Test
    void 스케줄_조회_비정상_케이스() throws Exception {
        mockMvc.perform(get("/reservations/available/schedule")
                        .param("performanceId", "-1")
                        .param("venueId", "2"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/reservations/available/schedule")
                        .param("performanceId", "1")
                        .param("venueId", "-2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 예약_정상_케이스() throws Exception {
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "scheduleId": 1,
                                "seatId": 101,
                                "userId": 1001
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(reservationFacade, times(1)).reserveSeat(1L, 101L, 1001L);
    }
}

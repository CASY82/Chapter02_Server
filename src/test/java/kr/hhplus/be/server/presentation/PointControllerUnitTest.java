package kr.hhplus.be.server.presentation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.presentation.api.v1.point.PointController;

@ExtendWith(MockitoExtension.class)
class PointControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private PointService pointService;

    @InjectMocks
    private PointController pointController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(pointController)
                .build();
    }

    @Test
    public void 포인트_충전_정상_케이스() throws Exception {
        when(pointService.chargePoint(1L, 500)).thenReturn(1500);

        mockMvc.perform(post("/point/balance/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "userId": 1,
                                "amount": 500
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("1500"));

        verify(pointService, times(1)).chargePoint(1L, 500);
    }

    @Test
    public void 포인트_조회_정상_케이스() throws Exception {
        when(pointService.getPoint(1L)).thenReturn(1000);

        mockMvc.perform(get("/point/balance/get")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));

        verify(pointService, times(1)).getPoint(1L);
    }
}


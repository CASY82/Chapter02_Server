package kr.hhplus.be.server.presentation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kr.hhplus.be.server.application.facade.PointFacade;
import kr.hhplus.be.server.presentation.api.v1.point.PointController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PointControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private PointFacade pointFacade;

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
        when(pointFacade.chargePoints("1", 500L)).thenReturn(1500L);

        mockMvc.perform(post("/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "userId": "1",
                                "amount": 500
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainPoint").value(1500));

        verify(pointFacade, times(1)).chargePoints("1", 500L);
    }

    @Test
    public void 포인트_조회_정상_케이스() throws Exception {
        when(pointFacade.getPointBalance("1")).thenReturn(1000L);

        mockMvc.perform(get("/points/balance")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainPoint").value(1000));

        verify(pointFacade, times(1)).getPointBalance("1");
    }
}
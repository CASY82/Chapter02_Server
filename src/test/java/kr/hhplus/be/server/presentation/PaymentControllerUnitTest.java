package kr.hhplus.be.server.presentation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.presentation.api.v1.payment.PaymentController;

@ExtendWith(MockitoExtension.class)
class PaymentControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentFacade paymentFacade;
    
    @InjectMocks
    private PaymentController paymentController;
    
    
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
        		.standaloneSetup(this.paymentController)
        		.build();
    }


    @Test
    public void 결제_정상_케이스_테스트() throws Exception {
        when(paymentFacade.paymentProcess(1L, 1000)).thenReturn(true);

        mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": 1,
                        "amount": 1000
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(paymentFacade, times(1)).paymentProcess(1L, 1000);
    }

    @Test
    public void 결제_실패_케이스_테스트() throws Exception {
        when(paymentFacade.paymentProcess(1L, 1000)).thenReturn(false);

        mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": 1,
                        "amount": 1000
                    }
                """))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("false"));

        verify(paymentFacade, times(1)).paymentProcess(1L, 1000);
    }

    @Test
    public void 결제_비정상_유저ID_테스트() throws Exception {
        mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": -1,
                        "amount": 1000
                    }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 결제_비정상_금액_테스트() throws Exception {
        mockMvc.perform(post("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "userId": 1,
                        "amount": -100
                    }
                """))
                .andExpect(status().isBadRequest());
    }
}



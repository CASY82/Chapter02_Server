package kr.hhplus.be.server.presentation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import kr.hhplus.be.server.application.obj.PaymentCommand;
import kr.hhplus.be.server.application.obj.PaymentResult;
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
        PaymentResult mockResult = new PaymentResult();
        mockResult.setPaymentStatus("COMPLETED");
        mockResult.setRemainPoint(5000L);
        
        PaymentCommand command = new PaymentCommand();
        command.setUserId("user123");
        command.setReservationId(1L);

        when(paymentFacade.pay(command)).thenReturn(mockResult);

        mockMvc.perform(post("/reservations/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "userId": "user123",
                                "reservationId": 1
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.remainPoint").value(5000));

        verify(paymentFacade, times(1)).pay(command);
    }

    @Test
    public void 결제_실패_케이스_테스트() throws Exception {
    	PaymentCommand command = new PaymentCommand();
        command.setUserId("user123");
        command.setReservationId(999L);
    	
        when(paymentFacade.pay(command))
                .thenThrow(new IllegalArgumentException("Reservation does not belong to user"));

        mockMvc.perform(post("/reservations/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "userId": "user123",
                                "reservationId": 999
                            }
                        """))
                .andExpect(status().isBadRequest());

        verify(paymentFacade, times(1)).pay(command);
    }

    @Test
    public void 결제_비정상_유저ID_테스트() throws Exception {
        mockMvc.perform(post("/reservations/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "userId": "",
                                "reservationId": 1
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 결제_비정상_예약ID_테스트() throws Exception {
        mockMvc.perform(post("/reservations/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "userId": "user123",
                                "reservationId": -10
                            }
                        """))
                .andExpect(status().isBadRequest());
    }
}



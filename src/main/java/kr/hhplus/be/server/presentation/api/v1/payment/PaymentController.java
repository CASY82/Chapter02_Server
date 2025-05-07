package kr.hhplus.be.server.presentation.api.v1.payment;

import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.application.obj.PaymentCommand;
import kr.hhplus.be.server.application.obj.PaymentResult;
import kr.hhplus.be.server.presentation.api.v1.obj.PaymentRequest;
import kr.hhplus.be.server.presentation.api.v1.obj.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final PaymentFacade paymentFacade;

    /**
     * 예약 결제
     */
    @PostMapping("/reservations/pay")
    public ResponseEntity<PaymentResponse> payReservation(
            @RequestBody @Validated PaymentRequest request) {
    	PaymentCommand command = new PaymentCommand();
    	PaymentResponse response = new PaymentResponse();
    	
    	command.setUserId(request.getUserId());
    	command.setReservationId(request.getReservationId());
    	
        try {
            PaymentResult result = paymentFacade.pay(command);
            
            response.setPaymentStatus(result.getPaymentStatus());
            response.setRemainPoint(result.getRemainPoint());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

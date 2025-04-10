package kr.hhplus.be.server.presentation.api.v1.payment;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.facade.PaymentFacade;
import kr.hhplus.be.server.presentation.api.v1.obj.PaymentRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentFacade paymentFacade;
	
	@PostMapping("/payment")
	public ResponseEntity<Boolean> payment(@Valid @RequestBody PaymentRequest request) {
		if(request.getAmount() < 0) return  ResponseEntity.status(HttpStatusCode.valueOf(400)).body(false);
		if(request.getUserId() < 0) return  ResponseEntity.status(HttpStatusCode.valueOf(400)).body(false);
		
		boolean status = this.paymentFacade.paymentProcess(request.getUserId(), request.getAmount());
		
		
		if (status) {
			return ResponseEntity.ok(true);
		} else {
			return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(false);
		}
	}
}

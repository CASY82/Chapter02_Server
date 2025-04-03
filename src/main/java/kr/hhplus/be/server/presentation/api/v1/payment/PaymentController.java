package kr.hhplus.be.server.presentation.api.v1.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
	
	@PostMapping("/payment")
	public ResponseEntity<Boolean> payment(@RequestBody long userId) {
		return ResponseEntity.ok(true);
	}
}

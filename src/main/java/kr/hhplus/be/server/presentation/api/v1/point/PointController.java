package kr.hhplus.be.server.presentation.api.v1.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointController {
	
	@PostMapping("/point/balance/charge")
	public ResponseEntity<Integer> balanceCharge(@RequestBody int amount) {
		return ResponseEntity.ok(100);
	}
	
	@GetMapping("/point/balance/get")
	public ResponseEntity<Integer> getBalance(@RequestParam("userId") long userId) {
		return ResponseEntity.ok(10);
	}
	
}

package kr.hhplus.be.server.presentation.api.v1.point;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.presentation.api.v1.obj.PointRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PointController {
	
	private final PointService pointService;
	
	@PostMapping("/point/balance/charge")
	public ResponseEntity<Integer> balanceCharge(@RequestBody PointRequest request) {
		int remainPoint = this.pointService.chargePoint(request.getUserId(), request.getAmount());
		
		return ResponseEntity.ok(remainPoint);
	}
	
	@GetMapping("/point/balance/get")
	public ResponseEntity<Integer> getBalance(@RequestParam("userId") long userId) {
		int point = this.pointService.getPoint(userId);
		
		return ResponseEntity.ok(point);
	}
	
}

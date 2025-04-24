package kr.hhplus.be.server.presentation.api.v1.point;

import kr.hhplus.be.server.application.facade.PointFacade;
import kr.hhplus.be.server.presentation.api.v1.obj.PointChargeRequest;
import kr.hhplus.be.server.presentation.api.v1.obj.PointChargeResponse;
import kr.hhplus.be.server.presentation.api.v1.obj.PointBalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor
public class PointController {
	
    private final PointFacade pointFacade;
	
    /**
     * 포인트 충전
     */
    @PostMapping("/points/charge")
    public ResponseEntity<PointChargeResponse> chargePoints(
            @RequestBody @Validated PointChargeRequest request) {
        try {
            Long remainPoint = pointFacade.chargePoints(
                    request.getUserId(),
                    request.getAmount()
            );

            PointChargeResponse response = new PointChargeResponse();
            response.setRemainPoint(remainPoint);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
	
    /**
     * 포인트 잔액 조회
     */
    @GetMapping("/points/balance")
    public ResponseEntity<PointBalanceResponse> getPointBalance(
            @RequestParam @NotBlank(message = "User ID must not be blank") String userId) {
        try {
            Long remainPoint = pointFacade.getPointBalance(userId);

            PointBalanceResponse response = new PointBalanceResponse();
            response.setRemainPoint(remainPoint);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
	
}

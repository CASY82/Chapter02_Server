package kr.hhplus.be.server.presentation.api.v1.token;

import kr.hhplus.be.server.domain.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    // 현재 순번 확인
    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponse> getQueueStatus(@RequestHeader("Authorization") String authorization) {
        QueueStatusResponse response = tokenService.getQueueStatus(authorization);
        return ResponseEntity.ok(response);
    }

    public record QueueStatusResponse(int position, boolean canEnter, String message) {
        public QueueStatusResponse(int position, boolean canEnter) {
            this(position, canEnter, null);
        }
    }
}
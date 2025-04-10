package kr.hhplus.be.server.presentation.api.v1.token;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.infrastructure.queue.QueueStore;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class TokenController {
	
    private final TokenService tokenService;
    private final QueueStore queueStore;

    // 토큰 발급 + 대기열 진입
    @PostMapping("/token")
    public ResponseEntity<String> issueToken(@RequestParam Long userId) {
        int position = this.queueStore.enterQueue(userId);
        String token = this.tokenService.issueToken(userId, String.valueOf(position));
        return ResponseEntity.ok(token);
    }

    // 현재 순번 확인
    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponse> getQueueStatus(@RequestParam Long userId) {
        int position = this.queueStore.getPosition(userId);
        boolean enterable = this.queueStore.isNowEnterable(userId, 3); // 앞 3명만 입장 가능
        return ResponseEntity.ok(new QueueStatusResponse(position, enterable));
    }

    public record QueueStatusResponse(int position, boolean canEnter) {}
}

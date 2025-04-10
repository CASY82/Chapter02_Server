package kr.hhplus.be.server.application.interceptor;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.infrastructure.queue.QueueStore;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueValidationInterceptor implements HandlerInterceptor {

	private final TokenService tokenService;
    private final QueueStore queueStore;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String jwt = request.getHeader("X-QUEUE-TOKEN");
        if (jwt == null || !tokenService.validateTokenValue(jwt)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing token");
            return false;
        }

        Long userId = this.tokenService.extractUserId(jwt); 
        if (!this.queueStore.isNowEnterable(userId, 3)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Still in queue");
            return false;
        }

        return true;
    }
}
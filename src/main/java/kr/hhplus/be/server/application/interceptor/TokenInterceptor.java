package kr.hhplus.be.server.application.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.domain.token.Token;
import kr.hhplus.be.server.domain.token.TokenService;
import kr.hhplus.be.server.infrastructure.queue.QueueStore;
import kr.hhplus.be.server.presentation.scheduler.QueueMonitorScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {
    private final TokenService tokenService;
    private final QueueStore queueStore;
    private final QueueMonitorScheduler queueMonitorScheduler;

    @Value("${queue.max-enterable:3}")
    private int maxEnterable;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tokenValue = request.getHeader("Authorization");
        if (tokenValue == null || tokenValue.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing Authorization header");
            return false;
        }

        if (tokenValue.startsWith("Bearer ")) {
            tokenValue = tokenValue.substring(7);
        }

        try {
            tokenService.validateToken(tokenValue);
            Token token = tokenService.getTokenByValue(tokenValue);
            if (!queueStore.isNowEnterable(token, maxEnterable)) {
                queueMonitorScheduler.requestEnterQueue(token);
                int position = queueStore.getPosition(token);
                response.setStatus(HttpStatus.ACCEPTED.value());
                response.getWriter().write("Request queued at position: " + position);
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(e.getMessage());
            return false;
        }
    }
}
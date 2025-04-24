package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final UserService userService;
    private final PointService pointService;

    @Transactional
    public Long chargePoints(String userId, Long amount) {
        // 사용자 확인
        User user = userService.getUser(userId);

        // 포인트 조회 또는 생성
        Point point = pointService.getOrCreatePoint(user);

        // 포인트 충전
        point = pointService.chargePoint(user.getId(), amount.intValue());

        return point.getRemainPoint().longValue();
    }
    
    public Long getPointBalance(String userId) {
        User user = userService.getUser(userId);
        Integer balance = pointService.getPointBalance(user.getId());
        return balance.longValue();
    }
}
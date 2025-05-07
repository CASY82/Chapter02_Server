package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.application.obj.PointCommand;
import kr.hhplus.be.server.application.obj.PointResult;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 포인트와 유저간 서비스 통합을 위한 포인트 전용 파사드
 * 1. 포인트 충전
 * 2. 잔여 포인트 확인
 */
@Component
@RequiredArgsConstructor
public class PointFacade {
    private final UserService userService;
    private final PointService pointService;

    @Transactional
    public PointResult chargePoints(PointCommand command) {
    	PointResult result = new PointResult();
    	
        // 사용자 확인
        User user = userService.getUser(command.getUserId());

        // 포인트 조회 또는 생성
        Point point = pointService.getOrCreatePoint(user);

        // 포인트 충전
        point = pointService.chargePoint(user.getId(), command.getAmount().intValue());

        result.setRemainPoint(point.getRemainPoint().longValue());
        
        return result;
    }
    
    public PointResult getPointBalance(PointCommand command) {
    	PointResult result = new PointResult();
    	
        User user = userService.getUser(command.getUserId());
        Integer balance = pointService.getPointBalance(user.getId());
        
        result.setRemainPoint(balance.longValue());
        return result;
    }
}
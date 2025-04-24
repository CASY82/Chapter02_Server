package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    public Point getOrCreatePoint(User user) {
        return pointRepository.findByUserRefId(user.getId())
                .orElseGet(() -> {
                    Point point = new Point();
                    point.setUserRefId(user.getId());
                    point.setRemainPoint(0);
                    return pointRepository.save(point);
                });
    }

    public Point chargePoint(Long userRefId, Integer amount) {
        Point point = pointRepository.findByUserRefId(userRefId)
                .orElseThrow(() -> new IllegalArgumentException("Point not found for userRefId: " + userRefId));
        point.charge(amount);
        return pointRepository.save(point);
    }
    
    public Integer getPointBalance(Long userRefId) {
        return pointRepository.findByUserRefId(userRefId)
                .map(Point::getRemainPoint)
                .orElse(0);
    }
    
    public Point usePoint(Long userRefId, Integer amount) {
        Point point = pointRepository.findByUserRefId(userRefId)
                .orElseThrow(() -> new IllegalArgumentException("Point not found for userRefId: " + userRefId));
        if (point.getRemainPoint() < amount) {
            throw new IllegalArgumentException("Insufficient points");
        }
        point.use(amount);
        return pointRepository.save(point);
    }
}

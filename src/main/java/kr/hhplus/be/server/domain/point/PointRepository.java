package kr.hhplus.be.server.domain.point;

import java.util.Optional;

public interface PointRepository {
    Optional<Point> findByUserRefId(Long userRefId);
    Optional<Point> findByUserRefIdWithLock(Long userRefId);
    Point save(Point point);
}

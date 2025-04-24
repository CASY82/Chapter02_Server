package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.PointJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PointDomainRepository implements PointRepository {

    private final PointJpaRepository repository;

    @Override
    public Optional<Point> findByUserRefId(Long userRefId) {
        return repository.findByUserRefId(userRefId);
    }

    @Override
    public Point save(Point point) {
        return repository.save(point);
    }
}
package kr.hhplus.be.server.infrastructure.jpa.repository;

import kr.hhplus.be.server.domain.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<Point, Long> {
    
	@Query("SELECT p FROM Point p WHERE p.userRefId = :userRefId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<Point> findByUserRefIdWithLock(@Param("userRefId") Long userRefId);
	
	Optional<Point> findByUserRefId(Long userRefId);
}
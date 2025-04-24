package kr.hhplus.be.server.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import kr.hhplus.be.server.domain.venue.Venue;

import java.util.Optional;

public interface VenueJpaRepository extends JpaRepository<Venue, Long> {
    @Query("SELECT v FROM Venue v WHERE v.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<Venue> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT v FROM Venue v WHERE v.id = :id")
    Optional<Venue> findById(@Param("id") Long id);
}
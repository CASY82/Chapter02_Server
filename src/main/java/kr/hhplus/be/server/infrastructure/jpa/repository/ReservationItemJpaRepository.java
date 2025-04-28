package kr.hhplus.be.server.infrastructure.jpa.repository;

import kr.hhplus.be.server.domain.reservationitem.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface ReservationItemJpaRepository extends JpaRepository<ReservationItem, Long> {
    List<ReservationItem> findByReservation_ReservationId(Long reservationId);

    @Query("SELECT ri FROM ReservationItem ri WHERE ri.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<ReservationItem> findByIdWithLock(@Param("id") Long id);
}
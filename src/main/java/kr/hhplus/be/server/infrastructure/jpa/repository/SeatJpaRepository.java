package kr.hhplus.be.server.infrastructure.jpa.repository;

import kr.hhplus.be.server.domain.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByVenueRefId(Long venueRefId);
    Optional<Seat> findBySeatId(Long seatId);
}
package kr.hhplus.be.server.domain.venue;

import java.util.Optional;

public interface VenueRepository {
    Optional<Venue> findById(Long id);
    Optional<Venue> findByIdWithLock(Long id);
    Venue save(Venue venue);
}
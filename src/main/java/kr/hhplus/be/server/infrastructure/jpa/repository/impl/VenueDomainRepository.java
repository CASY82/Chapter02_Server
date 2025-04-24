package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import kr.hhplus.be.server.domain.venue.Venue;
import kr.hhplus.be.server.domain.venue.VenueRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.VenueJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VenueDomainRepository implements VenueRepository {

    private final VenueJpaRepository repository;

    @Override
    public Optional<Venue> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Venue> findByIdWithLock(Long id) {
        return repository.findByIdWithLock(id);
    }

    @Override
    public Venue save(Venue venue) {
        return repository.save(venue);
    }
}
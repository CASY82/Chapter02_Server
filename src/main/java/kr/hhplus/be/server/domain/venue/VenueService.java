package kr.hhplus.be.server.domain.venue;

import kr.hhplus.be.server.infrastructure.jpa.repository.impl.VenueDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueDomainRepository repository;

    @Transactional(readOnly = true)
    public Venue getVenue(Long venueId) {
        return repository.findById(venueId)
                .orElseThrow(() -> new IllegalArgumentException("Venue not found"));
    }

    @Transactional
    public Venue updateVenue(Long venueId, String venueName, String location, Integer capacity) {
        Venue venue = repository.findByIdWithLock(venueId)
                .orElseThrow(() -> new IllegalArgumentException("Venue not found"));
        venue.setVenueName(venueName);
        venue.setLocation(location);
        venue.setCapacity(capacity);
        return repository.save(venue);
    }
}

package kr.hhplus.be.server.domain.venue;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VenueService {
	
	private final VenueRepository repository;
	
	public Venue getVenue(Long venueId) {
		return this.repository.findById(venueId);
	}

}

package kr.hhplus.be.server.domain.venue;

import java.time.Instant;

public class Venue {
	private Long id;
	private Long venueId;
	private String venueName;
	private String location;
	private Integer capacity;
	private Instant createdAt;
	private Instant updatedAt;
}

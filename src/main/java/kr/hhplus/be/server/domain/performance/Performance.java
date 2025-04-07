package kr.hhplus.be.server.domain.performance;

import java.time.Instant;

public class Performance {
	private Long id;
	private Long performanceId;
	private Long venueId;
	private String performanceName;
	private String description;
	private Instant startDate;
	private Instant endDate;
	private Instant createdAt;
	private Instant updatedAt;
}

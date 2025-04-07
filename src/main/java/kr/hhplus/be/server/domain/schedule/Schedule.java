package kr.hhplus.be.server.domain.schedule;

import java.time.Instant;

public class Schedule {
	private Long id;
	private Long scheduleId;
	private Long performanceRefId;
	private Long venueRefId;
	private Integer availableSeats;
	private Instant scheduleDateTime;
	private Instant createdAt;
	private Instant updatedAt;
}

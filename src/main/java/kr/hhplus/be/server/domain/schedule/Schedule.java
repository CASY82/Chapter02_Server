package kr.hhplus.be.server.domain.schedule;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
	private Long id;
	private Long scheduleId;
	private Long performanceRefId;
	private Long venueRefId;
	private String date;
	private Instant createdAt;
	private Instant updatedAt;

}

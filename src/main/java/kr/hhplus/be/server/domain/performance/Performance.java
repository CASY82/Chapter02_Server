package kr.hhplus.be.server.domain.performance;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Performance {
	private Long id;
	private Long performanceId;
	private Long venueRefId;
	private String performanceName;
	private String description;
	private Instant startDate;
	private Instant endDate;
	private Instant createdAt;
	private Instant updatedAt;
}

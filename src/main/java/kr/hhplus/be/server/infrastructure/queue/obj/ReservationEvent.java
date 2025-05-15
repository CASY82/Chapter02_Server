package kr.hhplus.be.server.infrastructure.queue.obj;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationEvent {
	private final Long performanceId;
	private final Long scheduleId;
	private final Long totalSeats;
	private final Long reservedSeats;
}

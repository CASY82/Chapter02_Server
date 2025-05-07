package kr.hhplus.be.server.application.obj;

import lombok.Data;

@Data
public class ReservationCheckCommand {
	private Long performanceId;
	private Long scheduleId;
}

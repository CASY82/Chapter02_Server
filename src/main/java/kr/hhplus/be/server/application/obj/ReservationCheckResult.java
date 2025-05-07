package kr.hhplus.be.server.application.obj;

import java.util.List;

import kr.hhplus.be.server.domain.schedule.Schedule;
import lombok.Data;

@Data
public class ReservationCheckResult {
	private List<Schedule> scheduleList;
	private List<Long> seatIds;
}

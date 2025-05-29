package kr.hhplus.be.server.presentation.event.obj;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatReservedEvent{
	private Long reservationId;
	private Long scheduleId;
	private List<Long> seatIds;
}

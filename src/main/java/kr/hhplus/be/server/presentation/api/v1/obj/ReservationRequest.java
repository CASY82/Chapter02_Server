package kr.hhplus.be.server.presentation.api.v1.obj;

import lombok.Data;

@Data
public class ReservationRequest {
	Long scheduleId;
	Long seatId;
	Long userId;
}

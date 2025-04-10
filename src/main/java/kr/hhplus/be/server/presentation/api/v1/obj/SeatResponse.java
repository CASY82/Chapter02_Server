package kr.hhplus.be.server.presentation.api.v1.obj;

import java.util.List;

import lombok.Data;

@Data
public class SeatResponse {
	List<Long> seatIds;
}

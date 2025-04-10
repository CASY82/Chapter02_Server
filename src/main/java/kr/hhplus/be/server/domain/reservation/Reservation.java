package kr.hhplus.be.server.domain.reservation;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
	private Long id;
	private Long reservationId;
	private Long userRefId;
	private Long orderRefId;
	private Long seatRefId;
	private Long scheduleRefId;
	private String reserveStatus;
	private Instant createdAt;
	private Instant updatedAt;
}

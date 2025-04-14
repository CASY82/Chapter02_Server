package kr.hhplus.be.server.domain.reservationitem;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationItem {
	private Long id;
	private Long reservationRefId;
	private Long seatRefId;
	private Instant createdAt;
	private Instant updatedAt;
}


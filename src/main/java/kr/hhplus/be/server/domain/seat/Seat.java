package kr.hhplus.be.server.domain.seat;

import java.time.Instant;

public class Seat {
	private Long id;
	private Long seatId;
	private Long venueRefId;
	private String seatNumber;
	private String seatRow;
	private String seatColumn;
	private Boolean reserved;
	private Instant createdAt;
	private Instant updatedAt;
}

package kr.hhplus.be.server.domain.reservation;

import java.time.Instant;

public class Reservation {
	private Long id; // DB 식별자
	private Long reservationId; // 도메인 식별자
	private Long userRefId;
	private Long orderRefId;
	private Long seatRefId;
	private Long scheduleRefId;
	private Instant createdAt;
	private Instant updatedAt;
}

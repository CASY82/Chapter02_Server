package kr.hhplus.be.server.domain.seat;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
	private Long id;
	private Long seatId;
	private Long venueRefId;
	private Long userRefId;
	// 원 설계 상으로 공연장 관계로 좌석을 가져와야 하나, 요구사항 정의 분석 결과
	// 날짜와 관계로 가져오도록 타협한 컬럼
	private Long scheduleRefId;
	private String seatNumber;
	private String seatRow;
	private String seatColumn;
	private Boolean reserved;
	private Instant createdAt;
	private Instant updatedAt;
	
	// 좌석 예약 가능 여부
	public boolean isReservable() {
		return !Boolean.TRUE.equals(reserved);
	}

	// 예약 상태 변경
	public void reserve() {
		this.reserved = true;
		this.updatedAt = Instant.now();
	}

	// 비예약 상태 변경
	public void unmarkReserved() {
		this.reserved = false;
		this.updatedAt = Instant.now();
	}
}

package kr.hhplus.be.server.domain.seat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seat")
public class Seat extends BaseEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_id", nullable = false, unique = true)
    private Long seatId;

    @Column(name = "venue_ref_id", nullable = false)
    private Long venueRefId;

    @Column(name = "user_ref_id", nullable = true) // 예약되지 않은 좌석은 null일 수 있음
    private Long userRefId;

    // 원 설계 상으로 공연장 관계로 좌석을 가져와야 하나, 요구사항 정의 분석 결과
    // 날짜와 관계로 가져오도록 타협한 컬럼
    @Column(name = "schedule_ref_id", nullable = false)
    private Long scheduleRefId;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "seat_row", nullable = false)
    private String seatRow;

    @Column(name = "seat_column", nullable = false)
    private String seatColumn;

    @Column(name = "reserved", nullable = false)
    private Boolean reserved;
	
	// 좌석 예약 가능 여부
	public boolean isReservable() {
		return !Boolean.TRUE.equals(reserved);
	}

	// 예약 상태 변경
	public void reserve() {
		this.reserved = true;
	}

	// 비예약 상태 변경
	public void unmarkReserved() {
		this.reserved = false;
	}
}

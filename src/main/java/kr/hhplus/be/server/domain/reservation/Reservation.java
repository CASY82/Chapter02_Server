package kr.hhplus.be.server.domain.reservation;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.reservationitem.ReservationItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservation")
public class Reservation extends BaseEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false, unique = true)
    private Long reservationId;

    @Column(name = "user_ref_id", nullable = false)
    private Long userRefId;

    @Column(name = "order_ref_id", nullable = false)
    private Long orderRefId;

    @Column(name = "seat_ref_id", nullable = false)
    private Long seatRefId;

    @Column(name = "schedule_ref_id", nullable = false)
    private Long scheduleRefId;

    @Column(name = "reserve_status", nullable = false)
    private ReservationStatus reserveStatus;
    
    @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100) // N+1 문제 완화를 위해 배치 사이즈 설정
    private List<ReservationItem> reservationItems = new ArrayList<>();
    
    // 좌석 예약 가능 여부
 	public boolean isReservable() {
 		return !ReservationStatus.COMPLETED.equals(reserveStatus);
 	}

 	// 예약 완료 상태 변경
 	public void reserve() {
 		this.reserveStatus = ReservationStatus.COMPLETED;
 	}

 	// 예약 취소 상태 변경
 	public void cancel() {
 		this.reserveStatus = ReservationStatus.CANCEL;
 	}
}

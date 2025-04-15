package kr.hhplus.be.server.domain.reservation;

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
    private String reserveStatus;
}

package kr.hhplus.be.server.domain.reservationitem;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.reservation.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservation_item")
public class ReservationItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_ref_id", referencedColumnName = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "seat_ref_id", nullable = false)
    private Long seatRefId;

    @Column(name = "schedule_ref_id", nullable = false)
    private Long scheduleRefId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;
}
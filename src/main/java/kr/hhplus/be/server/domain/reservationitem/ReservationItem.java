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
@Table(
    name = "reservation_item",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"reservation_ref_id", "seat_ref_id"})
    }
)
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

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Version
    private Long version;
}
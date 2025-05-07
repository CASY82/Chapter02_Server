package kr.hhplus.be.server.domain.seatreservation;

import java.time.Instant;

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
@Table(name = "seat_reservation")
public class SeatReservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_ref_id", nullable = false)
    private Long seatRefId;

    @Column(name = "reservation_ref_id", nullable = false)
    private Long reservationRefId;

    @Column(name = "reserved", nullable = false)
    private Boolean reserved;

    @Column(name = "reserved_at")
    private Instant reservedAt;
}
package kr.hhplus.be.server.domain.seatreservation;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime reservedAt;
}
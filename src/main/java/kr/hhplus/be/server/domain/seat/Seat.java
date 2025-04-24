package kr.hhplus.be.server.domain.seat;

import jakarta.persistence.*;
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

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "seat_row")
    private String seatRow;

    @Column(name = "seat_column")
    private String seatColumn;

    @Column(name = "version", nullable = false)
    private Integer version;
}
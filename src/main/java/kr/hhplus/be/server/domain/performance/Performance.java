package kr.hhplus.be.server.domain.performance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "performances") // 테이블 이름 명시
public class Performance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "performance_id", nullable = false, unique = true)
    private Long performanceId;

    @Column(name = "venue_ref_id", nullable = false)
    private Long venueRefId;

    @Column(name = "performance_name", nullable = false)
    private String performanceName;

    @Lob // 긴 텍스트 데이터를 저장할 때 사용
    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;
}


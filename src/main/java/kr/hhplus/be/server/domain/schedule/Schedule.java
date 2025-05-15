package kr.hhplus.be.server.domain.schedule;

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
@Table(name = "schedule")
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false, unique = true)
    private Long scheduleId;

    @Column(name = "performance_ref_id", nullable = false)
    private Long performanceRefId;

    @Column(name = "venue_ref_id", nullable = false)
    private Long venueRefId;

    @Column(name = "schedule_date_time", nullable = false)
    private Instant scheduleDateTime;
    
    @Column(name = "performance_start_time", nullable = false)
    private Instant performanceStartTime; // 공연 시작 시간
}
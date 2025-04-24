package kr.hhplus.be.server.infrastructure.jpa.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.schedule.Schedule;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByScheduleId(Long scheduleId);
    List<Schedule> findByPerformanceRefIdAndScheduleDateTimeAfter(Long performanceRefId, Instant scheduleDateTime);
}
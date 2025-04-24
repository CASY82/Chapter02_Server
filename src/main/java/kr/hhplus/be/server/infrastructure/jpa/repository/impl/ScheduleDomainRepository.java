package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.ScheduleJpaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduleDomainRepository implements ScheduleRepository {

    private final ScheduleJpaRepository repository;

    @Override
    public void save(Schedule schedule) {
        repository.save(schedule);
    }

    @Override
    public List<Schedule> findAllAvailableSchedules(Long performanceRefId) {
        return repository.findByPerformanceRefIdAndScheduleDateTimeAfter(performanceRefId, Instant.now());
    }

    @Override
    public Schedule findById(Long scheduleId) {
        return repository.findByScheduleId(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with scheduleId: " + scheduleId));
    }
}
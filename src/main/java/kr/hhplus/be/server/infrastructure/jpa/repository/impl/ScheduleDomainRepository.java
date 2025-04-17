package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

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
		this.repository.save(schedule);
	}

	@Override
	public List<Schedule> findAllAvailableSchedule(Long performanceRefId, Long venueRefId) {
		return this.repository.findAll();
	}

	@Override
	public Schedule findById(Long scheduleId) {
		return this.repository.findByScheduleId(scheduleId);
	}

}

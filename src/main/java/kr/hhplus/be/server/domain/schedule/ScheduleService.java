package kr.hhplus.be.server.domain.schedule;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleRepository repository;

	public List<Schedule> getScheduleList(Long performanceRefId, Long venueRefId) {
		return this.repository.findAllAvailableSchedule(performanceRefId, venueRefId);
	}
	
	public Schedule getSchedule(Long scheduleId) {
		return this.repository.findById(scheduleId);
	}
}


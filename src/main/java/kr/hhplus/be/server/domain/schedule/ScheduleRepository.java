package kr.hhplus.be.server.domain.schedule;

import java.util.List;

public interface ScheduleRepository {
	void save(Schedule schedule);
	List<Schedule> findAllAvailableSchedule(Long performanceRefId, Long venueRefId);
	Schedule findById(Long scheduleId);
}

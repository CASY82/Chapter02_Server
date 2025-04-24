package kr.hhplus.be.server.domain.schedule;

import java.util.List;

public interface ScheduleRepository {
    void save(Schedule schedule);
    List<Schedule> findAllAvailableSchedules(Long performanceRefId);
    Schedule findById(Long scheduleId);
}
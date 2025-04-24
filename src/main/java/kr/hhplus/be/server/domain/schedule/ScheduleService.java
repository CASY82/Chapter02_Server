package kr.hhplus.be.server.domain.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public List<Schedule> getAvailableSchedules(Long performanceRefId) {
        return scheduleRepository.findAllAvailableSchedules(performanceRefId);
    }

    public Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId);
    }
}
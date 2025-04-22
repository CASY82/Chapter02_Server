package kr.hhplus.be.server.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.schedule.Schedule;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {
	Schedule findByScheduleId(Long scheduleIf);
}

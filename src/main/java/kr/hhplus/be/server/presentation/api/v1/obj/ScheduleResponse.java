package kr.hhplus.be.server.presentation.api.v1.obj;

import java.time.Instant;
import java.util.List;

import lombok.Data;

@Data
public class ScheduleResponse {
    private List<ScheduleInfo> schedules;

    @Data
    public static class ScheduleInfo {
        private Long scheduleId;
        private Instant scheduleDateTime;
    }
}
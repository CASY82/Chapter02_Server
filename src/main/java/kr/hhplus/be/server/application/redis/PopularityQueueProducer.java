package kr.hhplus.be.server.application.redis;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.infrastructure.queue.obj.ReservationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularityQueueProducer {
    private static final String QUEUE_KEY = "popularity:queue";
    private static final String DLQ_KEY = "popularity:dlq";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Async
    @TransactionalEventListener
    public void handleReservationEvent(ReservationEvent event) {
        try {
            PopularityMessage message = new PopularityMessage(
                event.getPerformanceId(),
                event.getScheduleId(),
                event.getTotalSeats(),
                event.getReservedSeats()
            );
            String jsonMessage = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().leftPush(QUEUE_KEY, jsonMessage);
            log.info("Produced popularity update message: {}", jsonMessage);
        } catch (Exception e) {
            log.error("Failed to produce popularity update message: event={}", event, e);
            // DLQ에 메시지 저장
            try {
                String jsonMessage = objectMapper.writeValueAsString(new PopularityMessage(
                    event.getPerformanceId(),
                    event.getScheduleId(),
                    event.getTotalSeats(),
                    event.getReservedSeats()
                ));
                redisTemplate.opsForList().leftPush(DLQ_KEY, jsonMessage);
                log.info("Moved failed message to DLQ: {}", jsonMessage);
            } catch (Exception dlqEx) {
                log.error("Failed to move message to DLQ", dlqEx);
            }
            throw new RuntimeException("Failed to produce popularity update message", e);
        }
    }

    public static class PopularityMessage {
        private final Long performanceId;
        private final Long scheduleId;
        private final Long totalSeats;
        private final Long reservedSeats;

        public PopularityMessage(Long performanceId, Long scheduleId, Long totalSeats, Long reservedSeats) {
            this.performanceId = performanceId;
            this.scheduleId = scheduleId;
            this.totalSeats = totalSeats;
            this.reservedSeats = reservedSeats;
        }

        public Long getPerformanceId() {
            return performanceId;
        }

        public Long getScheduleId() {
            return scheduleId;
        }

        public Long getTotalSeats() {
            return totalSeats;
        }

        public Long getReservedSeats() {
            return reservedSeats;
        }
    }
}
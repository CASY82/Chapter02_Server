package kr.hhplus.be.server.application.ranking.event;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.application.period.PeriodType;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularityQueueConsumer {
    private static final String QUEUE_KEY = "popularity:queue";
    private static final String DLQ_KEY = "popularity:dlq";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ScheduleService scheduleService;

    @Scheduled(fixedRate = 500) // 0.5초마다 큐 확인
    public void consumePopularityUpdates() {
        List<String> jsonMessages = redisTemplate.opsForList().range(QUEUE_KEY, 0, 9);
        if (jsonMessages == null || jsonMessages.isEmpty()) {
            return;
        }

        for (String jsonMessage : jsonMessages) {
            try {
                PopularityQueueProducer.PopularityMessage message = objectMapper.readValue(jsonMessage, PopularityQueueProducer.PopularityMessage.class);
                processMessage(message);
                redisTemplate.opsForList().rightPop(QUEUE_KEY);
                log.info("Processed popularity update message: {}", jsonMessage);
            } catch (Exception e) {
                log.error("Failed to process popularity update message: {}", jsonMessage, e);
                redisTemplate.opsForList().leftPush(DLQ_KEY, jsonMessage);
                redisTemplate.opsForList().rightPop(QUEUE_KEY);
            }
        }
    }

    private void processMessage(PopularityQueueProducer.PopularityMessage message) {
        Long performanceId = message.getPerformanceId();
        Long scheduleId = message.getScheduleId();
        Long totalSeats = message.getTotalSeats();
        Long reservedSeats = message.getReservedSeats();
        Instant now = Instant.now();

        // 모든 시간 단위 처리
        for (PeriodType period : PeriodType.values()) {
            String infoKey = period.getInfoKey(performanceId, now);
            String rankingKey = period.getRankingKey(now);

            // 예약 좌석 수 갱신
            redisTemplate.opsForHash().putIfAbsent(infoKey, "totalSeats", totalSeats.toString());
            redisTemplate.opsForHash().increment(infoKey, "reservedSeats", 1);

            // 매진 여부 확인
            boolean isSoldOut = reservedSeats + 1 >= totalSeats;
            if (isSoldOut) {
                redisTemplate.opsForHash().put(infoKey, "isSoldOut", "true");
                redisTemplate.opsForHash().put(infoKey, "soldOutTime", String.valueOf(Instant.now().getEpochSecond()));
            }

            // 랭킹 스코어 계산
            double score;
            if (isSoldOut) {
                Object soldOutTimeObj = redisTemplate.opsForHash().get(infoKey, "soldOutTime");
                Long soldOutTime = soldOutTimeObj != null ? Long.valueOf(soldOutTimeObj.toString()) : Instant.now().getEpochSecond();
                Long startTime = getScheduleStartTime(scheduleId);
                score = soldOutTime - startTime;
            } else {
                double reservationRatio = (reservedSeats + 1.0) / totalSeats;
                score = reservationRatio * 1000;
            }

            // 랭킹 갱신
            redisTemplate.opsForZSet().add(rankingKey, performanceId.toString(), score);
            // TTL 설정
            redisTemplate.expire(rankingKey, period.getTtlSeconds(), TimeUnit.SECONDS);
            redisTemplate.expire(infoKey, period.getTtlSeconds(), TimeUnit.SECONDS);
        }
    }

    private Long getScheduleStartTime(Long scheduleId) {
        Schedule schedule = scheduleService.getSchedule(scheduleId);
        if (schedule == null || schedule.getPerformanceStartTime() == null) {
            log.warn("Schedule or performanceStartTime is null for scheduleId: {}", scheduleId);
            return Instant.now().getEpochSecond();
        }
        return schedule.getPerformanceStartTime().getEpochSecond();
    }
}
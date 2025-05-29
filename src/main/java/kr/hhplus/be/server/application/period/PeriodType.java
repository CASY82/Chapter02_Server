package kr.hhplus.be.server.application.period;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public enum PeriodType {
    DAILY("daily", 86400), // 24시간
    WEEKLY("weekly", 604800), // 7일
    MONTHLY("monthly", 2592000); // 30일

    private final String prefix;
    private final int ttlSeconds;

    PeriodType(String prefix, int ttlSeconds) {
        this.prefix = prefix;
        this.ttlSeconds = ttlSeconds;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getTtlSeconds() {
        return ttlSeconds;
    }

    public String getRankingKey(Instant instant) {
        LocalDate date = instant.atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
        switch (this) {
            case DAILY:
                return String.format("popularity:ranking:%s:%s", prefix, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            case WEEKLY:
                return String.format("popularity:ranking:%s:%s", prefix, date.format(DateTimeFormatter.ofPattern("yyyy-'W'ww")));
            case MONTHLY:
                return String.format("popularity:ranking:%s:%s", prefix, date.format(DateTimeFormatter.ofPattern("yyyyMM")));
            default:
                throw new IllegalStateException("Unsupported period type");
        }
    }

    public String getInfoKey(Long performanceId, Instant instant) {
        LocalDate date = instant.atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
        return String.format("performance:%d:info:%s:%s", performanceId, prefix, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }
}
package kr.hhplus.be.server.application.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularityRankingService {
    private static final String RANKING_KEY = "popularity:ranking";
    private static final String PERFORMANCE_INFO_KEY_PREFIX = "performance:%d:info";

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 상위 N개의 빠른 매진 공연 조회
     */
    public List<PopularityRank> getTopRankings(int limit) {
        return redisTemplate.opsForZSet()
            .reverseRangeWithScores(RANKING_KEY, 0, limit - 1)
            .stream()
            .map(entry -> {
                Long performanceId = Long.valueOf(entry.getValue());
                Double score = entry.getScore();
                String infoKey = String.format(PERFORMANCE_INFO_KEY_PREFIX, performanceId);
                Object isSoldOutObj = redisTemplate.opsForHash().get(infoKey, "isSoldOut");
                boolean isSoldOut = isSoldOutObj != null && Boolean.parseBoolean(isSoldOutObj.toString());
                return new PopularityRank(performanceId, score, isSoldOut);
            })
            .collect(Collectors.toList());
    }

    public static class PopularityRank {
        private final Long performanceId;
        private final Double score;
        private final Boolean isSoldOut;

        public PopularityRank(Long performanceId, Double score, Boolean isSoldOut) {
            this.performanceId = performanceId;
            this.score = score;
            this.isSoldOut = isSoldOut;
        }

        public Long getPerformanceId() {
            return performanceId;
        }

        public Double getScore() {
            return score;
        }

        public Boolean getIsSoldOut() {
            return isSoldOut;
        }
    }
}
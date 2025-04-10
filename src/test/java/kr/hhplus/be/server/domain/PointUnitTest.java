package kr.hhplus.be.server.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.domain.point.Point;

public class PointUnitTest {

    private Point point;

    @BeforeEach
    public void setup() {
        point = new Point(1L, 100L, 1000, Instant.now(), Instant.now());
    }

    @Test
    public void 포인트_충전_정상_테스트() {
        // given
        int chargeAmount = 500;

        // when
        point.charge(chargeAmount);

        // then
        assertEquals(1500, point.getRemainPoint(), "충전 후 포인트는 1500이어야 한다.");
    }

    @Test
    public void 포인트_충전_음수_테스트() {
        // given
        int chargeAmount = -100;

        // when
        point.charge(chargeAmount);

        // then
        assertEquals(1000, point.getRemainPoint(), "음수 충전은 무시되어야 하므로 포인트는 그대로여야 한다.");
    }

    @Test
    public void 포인트_사용_정상_테스트() {
        // given
        int useAmount = 300;

        // when
        point.use(useAmount);

        // then
        assertEquals(700, point.getRemainPoint(), "사용 후 포인트는 700이어야 한다.");
    }

    @Test
    public void 포인트_사용_음수_테스트() {
        // given
        int useAmount = -200;

        // when
        point.use(useAmount);

        // then
        assertEquals(1000, point.getRemainPoint(), "음수 사용은 무시되어야 하므로 포인트는 그대로여야 한다.");
    }
}

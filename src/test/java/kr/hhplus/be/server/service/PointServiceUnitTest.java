package kr.hhplus.be.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.PointService;

@ExtendWith(MockitoExtension.class)
class PointServiceUnitTest {

    @Mock
    private PointRepository repository;

    @InjectMocks
    private PointService pointService;

    private Point createTestPoint(Long userRefId, int remainPoint) {
        return new Point(
                1L,
                userRefId,
                remainPoint
        );
    }

    @Test
    void 잔액_조회_정상() {
        // given
        Long userRefId = 1L;
        Point point = createTestPoint(userRefId, 1000);
        when(repository.findByUserRefId(userRefId)).thenReturn(point);

        // when
        Integer balance = pointService.getPoint(userRefId);

        // then
        assertEquals(1000, balance);
    }

    @Test
    void 잔액_조회_잘못된_ID() {
        assertThrows(IllegalArgumentException.class, () -> pointService.getPoint(0L));
        assertThrows(IllegalArgumentException.class, () -> pointService.getPoint(-1L));
    }

    @Test
    void 포인트_충전_정상() {
        // given
        Long userRefId = 1L;
        Point point = createTestPoint(userRefId, 500);
        when(repository.findByUserRefId(userRefId)).thenReturn(point);

        // when
        Integer newBalance = pointService.chargePoint(userRefId, 300);

        // then
        assertEquals(800, newBalance);
        verify(repository).save(point);
    }

    @Test
    void 포인트_충전_음수() {
        // given
        Long userRefId = 1L;
        Point point = spy(createTestPoint(userRefId, 500));
        when(repository.findByUserRefId(userRefId)).thenReturn(point);

        // when
        Integer balance = pointService.chargePoint(userRefId, -100);

        // then
        assertEquals(500, balance); // 변화 없음
        verify(point).charge(-100);
        verify(repository).save(point);
    }

    @Test
    void 포인트_사용_정상() {
        // given
        Long userRefId = 1L;
        Point point = createTestPoint(userRefId, 500);
        when(repository.findByUserRefId(userRefId)).thenReturn(point);

        // when
        Integer newBalance = pointService.usePoint(userRefId, 200);

        // then
        assertEquals(300, newBalance);
        verify(repository).save(point);
    }

    @Test
    void 포인트_사용_음수() {
        // given
        Long userRefId = 1L;
        Point point = spy(createTestPoint(userRefId, 500));
        when(repository.findByUserRefId(userRefId)).thenReturn(point);

        // when
        Integer balance = pointService.usePoint(userRefId, -100);

        // then
        assertEquals(500, balance); // 변화 없음
        verify(point).use(-100);
        verify(repository).save(point);
    }

    @Test
    void 포인트_충전_또는_사용_잘못된_ID() {
        assertThrows(IllegalArgumentException.class, () -> pointService.chargePoint(null, 100));
        assertThrows(IllegalArgumentException.class, () -> pointService.chargePoint(0L, 100));

        assertThrows(IllegalArgumentException.class, () -> pointService.usePoint(null, 100));
        assertThrows(IllegalArgumentException.class, () -> pointService.usePoint(-1L, 100));
    }
}

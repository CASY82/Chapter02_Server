package kr.hhplus.be.server.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointService pointService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Point createPoint(Long userRefId, int remainPoint) {
        Point point = new Point();
        point.setUserRefId(userRefId);
        point.setRemainPoint(remainPoint);
        return point;
    }

    @Test
    void getOrCreatePoint_returnsExistingPoint() {
        // given
        User user = new User();
        user.setId(1L);
        Point existing = createPoint(1L, 100);
        when(pointRepository.findByUserRefId(1L)).thenReturn(Optional.of(existing));

        // when
        Point result = pointService.getOrCreatePoint(user);

        // then
        assertEquals(100, result.getRemainPoint());
    }

    @Test
    void getOrCreatePoint_createsNewIfNotExist() {
        // given
        User user = new User();
        user.setId(1L);
        Point created = createPoint(1L, 0);
        when(pointRepository.findByUserRefId(1L)).thenReturn(Optional.empty());
        when(pointRepository.save(any())).thenReturn(created);

        // when
        Point result = pointService.getOrCreatePoint(user);

        // then
        assertEquals(0, result.getRemainPoint());
        verify(pointRepository).save(any(Point.class));
    }

    @Test
    void chargePoint_increasesBalance() {
        // given
        Long userRefId = 1L;
        Point point = createPoint(userRefId, 200);
        when(pointRepository.findByUserRefId(userRefId)).thenReturn(Optional.of(point));
        when(pointRepository.save(point)).thenReturn(point);

        // when
        Point updated = pointService.chargePoint(userRefId, 100);

        // then
        assertEquals(300, updated.getRemainPoint());
    }

    @Test
    void chargePoint_throwsIfUserNotFound() {
        // given
        Long userRefId = 99L;
        when(pointRepository.findByUserRefId(userRefId)).thenReturn(Optional.empty());

        // expect
        assertThrows(IllegalArgumentException.class,
                () -> pointService.chargePoint(userRefId, 100));
    }

    @Test
    void getPointBalance_returnsCorrectBalance() {
        // given
        Long userRefId = 1L;
        Point point = createPoint(userRefId, 400);
        when(pointRepository.findByUserRefId(userRefId)).thenReturn(Optional.of(point));

        // when
        int balance = pointService.getPointBalance(userRefId);

        // then
        assertEquals(400, balance);
    }

    @Test
    void getPointBalance_returnsZeroIfNotFound() {
        // given
        Long userRefId = 2L;
        when(pointRepository.findByUserRefId(userRefId)).thenReturn(Optional.empty());

        // when
        int balance = pointService.getPointBalance(userRefId);

        // then
        assertEquals(0, balance);
    }

    @Test
    void usePoints_decreasesBalance() {
        // given
        Long userRefId = 1L;
        Point point = createPoint(userRefId, 500);
        when(pointRepository.findByUserRefIdWithLock(userRefId)).thenReturn(Optional.of(point));
        when(pointRepository.save(point)).thenReturn(point);

        // when
        Point result = pointService.usePoints(userRefId, 200);

        // then
        assertEquals(300, result.getRemainPoint());
    }

    @Test
    void usePoints_throwsIfUserNotFound() {
        // given
        Long userRefId = 1L;
        when(pointRepository.findByUserRefIdWithLock(userRefId)).thenReturn(Optional.empty());

        // expect
        assertThrows(IllegalStateException.class,
                () -> pointService.usePoints(userRefId, 100));
    }

    @Test
    void usePoints_throwsIfInsufficient() {
        // given
        Long userRefId = 1L;
        Point point = createPoint(userRefId, 100);
        when(pointRepository.findByUserRefIdWithLock(userRefId)).thenReturn(Optional.of(point));

        // when & then
        assertThrows(IllegalStateException.class, () -> pointService.usePoints(userRefId, 200));
    }
}


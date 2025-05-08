package kr.hhplus.be.server.infrastructure;

import kr.hhplus.be.server.application.obj.PaymentCommand;
import kr.hhplus.be.server.infrastructure.lock.DistributedLock;
import kr.hhplus.be.server.infrastructure.lock.DistributedLockAop;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistributedLockAopTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private DistributedLock distributedLock;

    @InjectMocks
    private DistributedLockAop distributedLockAop;

    private PaymentCommand paymentCommand;

    @BeforeEach
    void setUp() {
        // PaymentCommand 초기화
        paymentCommand = new PaymentCommand();
        paymentCommand.setUserId("testUser");
    }

    @Test
    void testLockAcquiredAndReleasedSuccessfully() throws Throwable {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(distributedLock.key()).thenReturn("'payLock:user:' + #command.userId");
        when(distributedLock.waitTime()).thenReturn(5L);
        when(distributedLock.leaseTime()).thenReturn(3L);
        when(distributedLock.timeUnit()).thenReturn(TimeUnit.SECONDS);
        when(joinPoint.getArgs()).thenReturn(new Object[]{paymentCommand});
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("success");

        // When
        Object result = distributedLockAop.lock(joinPoint, distributedLock);

        // Then
        assertEquals("success", result);
        verify(redissonClient).getLock("payLock:user:testUser");
        verify(lock).tryLock(5L, 3L, TimeUnit.SECONDS);
        verify(joinPoint).proceed();
        verify(lock).unlock();
    }

    @Test
    void testLockAcquisitionFailure() throws Throwable {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(distributedLock.key()).thenReturn("'payLock:user:' + #command.userId");
        when(distributedLock.waitTime()).thenReturn(5L);
        when(distributedLock.leaseTime()).thenReturn(3L);
        when(distributedLock.timeUnit()).thenReturn(TimeUnit.SECONDS);
        when(joinPoint.getArgs()).thenReturn(new Object[]{paymentCommand});
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> distributedLockAop.lock(joinPoint, distributedLock));
        assertEquals("Failed to acquire lock: payLock:user:testUser", exception.getMessage());
        verify(redissonClient).getLock("payLock:user:testUser");
        verify(lock).tryLock(5L, 3L, TimeUnit.SECONDS);
        verify(joinPoint, never()).proceed();
        verify(lock, never()).unlock();
    }

    @Test
    void testLockNotHeldByCurrentThread() throws Throwable {
        // Given
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(distributedLock.key()).thenReturn("'payLock:user:' + #command.userId");
        when(distributedLock.waitTime()).thenReturn(5L);
        when(distributedLock.leaseTime()).thenReturn(3L);
        when(distributedLock.timeUnit()).thenReturn(TimeUnit.SECONDS);
        when(joinPoint.getArgs()).thenReturn(new Object[]{paymentCommand});
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(false);
        when(joinPoint.proceed()).thenReturn("success");

        // When
        Object result = distributedLockAop.lock(joinPoint, distributedLock);

        // Then
        assertEquals("success", result);
        verify(redissonClient).getLock("payLock:user:testUser");
        verify(lock).tryLock(5L, 3L, TimeUnit.SECONDS);
        verify(joinPoint).proceed();
        verify(lock, never()).unlock();
    }

    @Test
    void testInvalidSpelExpression() throws Throwable {
        // Given
        when(distributedLock.key()).thenReturn("invalidSpelExpression");
        when(joinPoint.getArgs()).thenReturn(new Object[]{paymentCommand});

        // When & Then
        assertThrows(IllegalStateException.class, () -> distributedLockAop.lock(joinPoint, distributedLock));
        verify(redissonClient, never()).getLock(anyString());
        verify(lock, never()).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        verify(joinPoint, never()).proceed();
        verify(lock, never()).unlock();
    }

    @Test
    void testNullCommand() throws Throwable {
        // Given
        when(distributedLock.key()).thenReturn("'payLock:user:' + #command.userId");
        when(joinPoint.getArgs()).thenReturn(new Object[]{null});

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> distributedLockAop.lock(joinPoint, distributedLock));
        assertEquals("Lock key evaluation failed: command argument is null or missing", exception.getMessage());
        verify(redissonClient, never()).getLock(anyString());
        verify(lock, never()).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        verify(joinPoint, never()).proceed();
        verify(lock, never()).unlock();
    }
}
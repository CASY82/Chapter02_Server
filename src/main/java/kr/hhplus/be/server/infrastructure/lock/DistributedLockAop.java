package kr.hhplus.be.server.infrastructure.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Order(1)
@Component
public class DistributedLockAop {

    private final RedissonClient redissonClient;
    private final ExpressionParser spelParser;

    public DistributedLockAop(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.spelParser = new SpelExpressionParser();
    }

    @Around("@annotation(distributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        // SpEL로 락 키 평가
        String lockKey = evaluateLockKey(distributedLock.key(), joinPoint);

        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        TimeUnit timeUnit = distributedLock.timeUnit();

        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락 획득 시도
            boolean isLocked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (!isLocked) {
                throw new IllegalStateException("Failed to acquire lock: " + lockKey);
            }

            // 락 획득 성공, 비즈니스 로직 실행
            return joinPoint.proceed();
        } finally {
            // 락 해제 (락이 획득된 경우에만)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private String evaluateLockKey(String keyExpression, ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        if (args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Lock key evaluation failed: command argument is null or missing");
        }
        context.setVariable("command", args[0]);
        Expression expression = spelParser.parseExpression(keyExpression);
        try {
            String lockKey = expression.getValue(context, String.class);
            if (lockKey == null) {
                throw new IllegalStateException("Lock key evaluation resulted in null: " + keyExpression);
            }
            return lockKey;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to evaluate lock key: " + keyExpression, e);
        }
    }
}
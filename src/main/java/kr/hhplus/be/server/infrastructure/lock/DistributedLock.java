package kr.hhplus.be.server.infrastructure.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

	// 락 키 (SpEL 표현식 지원)
    String key();

    // 락 획득을 시도할 최대 대기 시간
    long waitTime() default 5L;

    // 락의 최대 유지 시간
    long leaseTime() default 3L;

    // 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
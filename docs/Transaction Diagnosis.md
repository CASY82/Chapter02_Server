# 콘서트 예약 시스템의 마이크로서비스 아키텍처 설계 및 트랜잭션 관리

## 1. 문서 정보
- **제목**: 콘서트 예약 시스템의 마이크로서비스 아키텍처 설계 및 트랜잭션 관리
- **작성자**: 최현호
- **작성일**: 2025년 5월 23일
- **도메인**: 콘서트 정보, 주문(좌석 예약), 결제, 리뷰, 사용자

## 2. 배경
콘서트 예약 시스템은 사용자, 예약, 결제, 좌석, 공연, 스케줄 등 여러 도메인을 포함합니다. 기존 모놀리식 구조에서는 서비스가 커질수록 빌드/배포 시간이 길어지고, 작은 변경이 전체 시스템에 영향을 미치는 문제가 발생했습니다. 이를 해결하기 위해 마이크로서비스 아키텍처(MSA)를 도입하여 도메인별로 배포 단위를 분리하고, 트랜잭션 관리 문제를 해결하는 방안을 설계합니다.

### 도메인 정의
- **콘서트 정보**: 공연(`performance`), 스케줄(`schedule`), 좌석(`seat`), 공연장(`venue`), 좌석 가용성(`seat_availability`).
- **주문(좌석 예약)**: 주문(`order`), 주문 항목(`order_item`), 예약(`reservation`), 예약 항목(`reservation_item`), 좌석 예약(`seat_reservation`).
- **결제**: 결제(`payment`), 포인트(`point`), 포인트 트랜잭션(`point_transaction`), 결제-포인트 연결(`payment_point`).
- **리뷰**: 공연 후 리뷰(ERD 미포함, 별도 서비스로 가정).
- **사용자**: 사용자(`user`), 토큰(`token`).

## 3. 결정
콘서트 예약 시스템을 마이크로서비스 아키텍처로 전환하며, 도메인별로 독립된 배포 단위를 설계하고, 트랜잭션 분리로 인한 문제를 코레오그래피(Choreography) 방식의 사가 패턴으로 해결합니다.

### 3.1. 마이크로서비스 아키텍처의 이점
1. **독립적 배포**:
   - 각 도메인(콘서트 정보, 주문, 결제 등)을 독립적으로 빌드/배포 가능.
   - 작은 변경(예: 결제 로직 수정)이 다른 도메인에 영향을 주지 않음.
2. **장애 격리**:
   - 특정 도메인 서비스(예: 결제 서비스)의 장애가 전체 시스템으로 확산되지 않음.
3. **도메인별 확장성**:
   - 트래픽이 높은 도메인(예: 좌석 예약)은 더 많은 서버 자원 할당 가능.
   - 예: 좌석 예약 서비스는 초당 높은 요청을 처리하기 위해 더 큰 서버 또는 더 많은 인스턴스 사용.
4. **데이터베이스 분리**:
   - 모놀리식 구조에서 단일 Oracle DB의 조회 성능 저하(밀리초~분 단위)를 해결하기 위해 도메인별로 DB를 분리(예: MySQL, Redis).
   - 예: 좌석 가용성은 Redis로 캐싱, 결제는 MySQL로 트랜잭션 처리.

### 3.2. 도메인별 배포 단위
각 도메인은 독립된 마이크로서비스로 구성되며, 아래와 같이 설계합니다.

1. **콘서트 정보 서비스**:
   - **책임**: 공연 정보, 스케줄, 좌석, 공연장 관리. 예약 가능 좌석 및 스케줄 조회.
   - **엔티티**: `performance`, `schedule`, `seat`, `venue`, `seat_availability`.
   - **DB**: MySQL (공연/스케줄 데이터), Redis (좌석 가용성 캐싱).
   - **API**:
     - `GET /performances`: 공연 목록 조회.
     - `GET /schedules/{performanceId}`: 예약 가능 스케줄 조회.
     - `GET /seats/{scheduleId}`: 예약 가능 좌석 조회.
2. **주문(좌석 예약) 서비스**:
   - **책임**: 주문 생성, 예약 생성, 좌석 예약 처리.
   - **엔티티**: `order`, `order_item`, `reservation`, `reservation_item`, `seat_reservation`.
   - **DB**: MySQL (주문/예약 데이터), Redis (좌석 예약 상태, TTL로 5분 만료).
   - **API**:
     - `POST /reservations`: 예약 요청.
     - `GET /reservations/{reservationId}`: 예약 상태 조회.
3. **결제 서비스**:
   - **책임**: 결제 처리, 포인트 차감, 결제 내역 생성.
   - **엔티티**: `payment`, `point`, `point_transaction`, `payment_point`.
   - **DB**: MySQL (결제/포인트 데이터), Redis (포인트 잔액 캐싱).
   - **API**:
     - `POST /payments`: 결제 처리.
     - `POST /points/charge`: 포인트 충전.
     - `GET /points/{userId}`: 포인트 잔액 조회.
4. **리뷰 서비스**:
   - **책임**: 공연 후 리뷰 작성/조회.
   - **엔티티**: 별도 리뷰 엔티티(ERD 미포함, 가정).
   - **DB**: MySQL.
   - **API**:
     - `POST /reviews`: 리뷰 작성.
     - `GET /reviews/{performanceId}`: 리뷰 조회.
5. **사용자 서비스**:
   - **책임**: 사용자 정보 관리, 토큰 발급/검증.
   - **엔티티**: `user`, `token`.
   - **DB**: MySQL (사용자 데이터), Redis (토큰 저장, TTL로 만료 처리).
   - **API**:
     - `GET /users/{userId}`: 사용자 정보 조회.
     - `POST /tokens`: 토큰 발급.
     - `GET /tokens/{tokenId}`: 토큰 상태 조회.

### 3.3. 트랜잭션 분리로 인한 문제
마이크로서비스로 전환 시, 도메인별로 DB가 분리되면서 아래 문제가 발생합니다:
1. **DB 간 조인 불가**:
   - 예: 주문 서비스에서 `order`와 결제 서비스의 `payment`를 조인하여 상태 확인 불가.
   - 해결: API 호출 또는 이벤트로 데이터 동기화.
2. **분산 트랜잭션 불가**:
   - 예: 결제 처리 시 `order` 상태 변경, `payment` 생성, `point` 차감이 단일 트랜잭션으로 처리 불가.
   - 결과: 일부 작업 실패 시 데이터 불일치 발생 가능.
3. **데이터 일관성 문제**:
   - 예: 결제 성공 후 포인트 차감 실패 시, 결제는 완료되었으나 포인트가 차감되지 않은 상태 발생.
4. **복잡한 에러 처리**:
   - 분산 환경에서 롤백 및 보상 트랜잭션 처리 필요.

### 3.4. 해결 방안: 코레오그래피 기반 사가 패턴
코레오그래피(Choreography) 방식의 사가 패턴을 사용하여 분산 트랜잭션을 관리합니다. 각 서비스는 독립적으로 로컬 트랜잭션을 처리하고, 이벤트를 발행하여 다음 작업을 트리거합니다. 실패 시 보상 트랜잭션(Compensating Transaction)을 발행하여 데이터 일관성을 유지합니다.

#### 사가 흐름 예시: 결제 처리
1. **주문 상태 변경** (주문 서비스):
   - 주문 상태를 `PENDING` → `PAYMENT_PROCESSING`으로 변경.
   - 로컬 트랜잭션 성공 시 `OrderStatusUpdatedEvent` 발행.
   - 실패 시: 프로세스 종료, 클라이언트에 에러 응답.
2. **결제 처리** (결제 서비스):
   - `OrderStatusUpdatedEvent` 수신 후 결제 생성(예: PG 연동).
   - 성공 시 `PaymentCompletedEvent` 발행.
   - 실패 시 `OrderRollbackEvent` 발행 (주문 상태를 `PENDING`으로 롤백).
3. **포인트 차감** (결제 서비스):
   - `PaymentCompletedEvent` 수신 후 포인트 차감.
   - 성공 시 `PointDeductedEvent` 발행.
   - 실패 시 `PaymentRollbackEvent` 발행 (결제 취소).
4. **알림 발송** (결제 서비스):
   - `PointDeductedEvent` 수신 후 결제 완료 알림 발송.
   - 성공 시: 프로세스 완료.
   - 실패 시: 로그 기록 (알림 실패는 비즈니스에 영향 없음).

#### 사가 흐름 예시: 좌석 예약
1. **예약 생성** (주문 서비스):
   - 예약 생성 및 좌석 임시 배정(5분 TTL).
   - 성공 시 `SeatReservedEvent` 발행.
   - 실패 시: 프로세스 종료.
2. **좌석 상태 업데이트** (콘서트 서비스):
   - `SeatReservedEvent` 수신 후 좌석 가용성 업데이트.
   - 성공 시 `ReservationConfirmedEvent` 발행.
   - 실패 시 `ReservationRollbackEvent` 발행 (예약 및 좌석 예약 해제).
3. **알림 발송** (결제 서비스):
   - `ReservationConfirmedEvent` 수신 후 예약 완료 알림 발송.
   - 성공 시: 프로세스 완료.
   - 실패 시: 로그 기록 (알림 실패는 비즈니스에 영향 없음).

### 3.5. 이벤트 기반 구현 (Kafka 활용)
Kafka를 사용하여 이벤트를 발행/소비하며, 각 서비스는 독립적으로 이벤트를 처리합니다.

#### 이벤트 정의
- `OrderStatusUpdatedEvent`: `{ orderId, status, updatedAt }`
- `PaymentCompletedEvent`: `{ paymentId, reservationId, userId, amount, paymentDate }`
- `PointDeductedEvent`: `{ userId, pointId, amount, transactionDate }`
- `SeatReservedEvent`: `{ reservationId, scheduleId, seatIds, reservedAt, expiresAt }`
- `ReservationConfirmedEvent`: `{ reservationId, scheduleId, seatIds }`
- `OrderRollbackEvent`: `{ orderId, reason }`
- `PaymentRollbackEvent`: `{ paymentId, reason }`
- `PointRollbackEvent`: `{ pointId, amount, reason }`
- `ReservationRollbackEvent`: `{ reservationId, reason }`

#### 사가 흐름 코드 예시 (결제 처리)
1. **주문 서비스**:
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));
        order.setOrderStatus(status);
        orderRepository.save(order);

        kafkaTemplate.send("order-topic", new OrderStatusUpdatedEvent(orderId, status, Instant.now()));
    }

    @KafkaListener(topics = "order-rollback-topic")
    @Transactional
    public void handleOrderRollback(OrderRollbackEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문 없음"));
        order.setOrderStatus("PENDING");
        orderRepository.save(order);
        log.info("주문 롤백 완료: {}", event.getOrderId());
    }
}
```

2. **결제 서비스 (결제 처리)**:
```java
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-topic")
    @Transactional
    public void processPayment(OrderStatusUpdatedEvent event) {
        try {
            Payment payment = new Payment();
            payment.setOrderRefId(event.getOrderId());
            payment.setAmount(getAmountFromOrder(event.getOrderId())); // 주문 서비스 API 호출
            payment.setPaymentStatus("COMPLETED");
            paymentRepository.save(payment);

            kafkaTemplate.send("payment-topic", new PaymentCompletedEvent(
                    payment.getId(), getReservationId(event.getOrderId()), payment.getUserRefId(), payment.getAmount(), Instant.now()));
        } catch (Exception e) {
            kafkaTemplate.send("order-rollback-topic", new OrderRollbackEvent(event.getOrderId(), e.getMessage()));
            log.error("결제 실패: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "payment-rollback-topic")
    @Transactional
    public void handlePaymentRollback(PaymentRollbackEvent event) {
        Payment payment = paymentRepository.findById(event.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 없음"));
        payment.setPaymentStatus("CANCELLED");
        paymentRepository.save(payment);
        log.info("결제 롤백 완료: {}", event.getPaymentId());
    }
}
```

3. **결제 서비스 (포인트 차감)**:
```java
@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "payment-topic")
    @Transactional
    public void deductPoint(PaymentCompletedEvent event) {
        try {
            Point point = pointRepository.findByUserId(event.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("포인트 없음"));
            if (point.getRemainPoint() < event.getAmount()) {
                throw new IllegalStateException("포인트 부족");
            }
            point.setRemainPoint(point.getRemainPoint() - event.getAmount());
            pointRepository.save(point);

            kafkaTemplate.send("point-topic", new PointDeductedEvent(event.getUserId(), point.getId(), event.getAmount(), Instant.now()));
        } catch (Exception e) {
            kafkaTemplate.send("payment-rollback-topic", new PaymentRollbackEvent(event.getPaymentId(), e.getMessage()));
            log.error("포인트 차감 실패: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "point-rollback-topic")
    @Transactional
    public void handlePointRollback(PointRollbackEvent event) {
        Point point = pointRepository.findById(event.getPointId())
                .orElseThrow(() -> new IllegalArgumentException("포인트 없음"));
        point.setRemainPoint(point.getRemainPoint() + event.getAmount());
        pointRepository.save(point);
        log.info("포인트 복구 완료: {}", event.getPointId());
    }
}
```

4. **결제 서비스 (알림 발송)**:
```java
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "point-topic")
    public void sendPaymentNotification(PointDeductedEvent event) {
        try {
            log.info("결제 완료 알림 발송 - 결제 ID: {}, 사용자 ID: {}", event.getPaymentId(), event.getUserId());
            // notifyService.sendPaymentNotification(event.getPaymentId(), event.getUserId());
        } catch (Exception e) {
            log.warn("결제 알림 발송 실패: {}", e.getMessage());
        }
    }
}
```

#### 사가 흐름 코드 예시 (좌석 예약)
1. **주문 서비스**:
```java
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatReservationRepository seatReservationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void createReservation(ReserveCommand command, Long userId) {
        Reservation reservation = new Reservation();
        reservation.setUserRefId(userId);
        reservation.setScheduleRefId(command.getScheduleId());
        reservation.setOrderRefId(command.getOrderId());
        reservation.setReserveStatus("PENDING");
        reservation.setReservationItems(createReservationItems(command));
        reservationRepository.save(reservation);

        for (Long seatId : command.getSeatId()) {
            SeatReservation seatReservation = new SeatReservation();
            seatReservation.setSeatRefId(seatId);
            seatReservation.setReservationRefId(reservation.getReservationId());
            seatReservation.setReserved(true);
            seatReservation.setReservedAt(Instant.now());
            seatReservation.setExpiresAt(Instant.now().plusSeconds(300));
            seatReservationRepository.save(seatReservation);
        }

        kafkaTemplate.send("reservation-topic", new SeatReservedEvent(
                reservation.getReservationId(), command.getScheduleId(), command.getSeatId(), Instant.now(), Instant.now().plusSeconds(300)));
    }

    @KafkaListener(topics = "reservation-rollback-topic")
    @Transactional
    public void handleReservationRollback(ReservationRollbackEvent event) {
        Reservation reservation = reservationRepository.findById(event.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약 없음"));
        reservation.setReserveStatus("CANCELLED");
        reservationRepository.save(reservation);

        seatReservationRepository.findByReservationId(event.getReservationId())
                .forEach(seat -> {
                    seat.setReserved(false);
                    seatReservationRepository.save(seat);
                });
        log.info("예약 롤백 완료: {}", event.getReservationId());
    }
}
```

2. **콘서트 서비스**:
```java
@Service
@RequiredArgsConstructor
public class ConcertService {
    private final SeatAvailabilityRepository seatAvailabilityRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "reservation-topic")
    @Transactional
    public void confirmReservation(SeatReservedEvent event) {
        try {
            SeatAvailability availability = seatAvailabilityRepository.findByScheduleId(event.getScheduleId())
                    .orElseThrow(() -> new IllegalArgumentException("스케줄 없음"));
            availability.setAvailableSeats(availability.getAvailableSeats() - event.getSeatIds().size());
            seatAvailabilityRepository.save(availability);

            kafkaTemplate.send("reservation-confirmed-topic", new ReservationConfirmedEvent(
                    event.getReservationId(), event.getScheduleId(), event.getSeatIds()));
        } catch (Exception e) {
            kafkaTemplate.send("reservation-rollback-topic", new ReservationRollbackEvent(event.getReservationId(), e.getMessage()));
            log.error("예약 확인 실패: {}", e.getMessage());
        }
    }
}
```

3. **결제 서비스 (알림 발송)**:
```java
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "reservation-confirmed-topic")
    public void sendReservationNotification(ReservationConfirmedEvent event) {
        try {
            log.info("예약 완료 알림 발송 - 예약 ID: {}", event.getReservationId());
            // notifyService.sendReservationNotification(event.getReservationId(), event.getSeatIds());
        } catch (Exception e) {
            log.warn("예약 알림 발송 실패: {}", e.getMessage());
        }
    }
}
```

## 4. 결정의 결과
- **장점**:
  - **독립성**: 각 서비스(콘서트, 주문, 결제, 리뷰)가 독립적으로 배포/확장 가능.
  - **장애 격리**: 결제 서비스 장애가 예약 서비스에 영향을 주지 않음.
  - **유연성**: 도메인별로 최적화된 DB 및 캐싱 전략 사용(Redis for 좌석/포인트, MySQL for 영속 데이터).
  - **일관성**: 코레오그래피 사가로 데이터 일관성 유지, 보상 트랜잭션으로 롤백 처리.
- **단점**:
  - **복잡성 증가**: 이벤트 발행/소비 로직 및 롤백 처리로 코드 복잡도 증가.
  - **지연 시간**: Kafka 이벤트 전파로 약간의 지연 발생 가능.
  - **모니터링 필요**: 이벤트 실패 시 로그 및 모니터링 시스템 필수.
- **해결책**:
  - **모니터링**: Kafka 토픽 모니터링 및 실패 이벤트 재처리 메커니즘 도입.
  - **재시도**: 실패 이벤트는 Dead Letter Queue(DLQ)에 저장 후 재시도.
  - **테스트**: 분산 환경에서의 사가 테스트를 위한 통합 테스트 작성.

## 5. 대안
- **오케스트레이션 사가**:
  - 중앙 오케스트레이터가 모든 단계를 관리.
  - 장점: 흐름 제어가 명확, 롤백 로직 단순화.
  - 단점: 오케스트레이터가 단일 장애 지점(SPOF) 가능, 서비스 간 결합도 증가.
  - 이유: 코레오그래피를 선택한 이유는 서비스 간 독립성을 유지하고, 각 서비스가 자체적으로 책임을 다하도록 하기 위함.
- **2PC (Two-Phase Commit)**:
  - 분산 트랜잭션을 단일 트랜잭션으로 처리.
  - 단점: 성능 저하, 복잡한 롤백 처리, 서비스 간 강한 결합.
  - 이유: MSA의 확장성과 유연성을 저해하므로 배제.
- **모놀리식 유지**:
  - 단일 DB와 애플리케이션 유지.
  - 단점: 배포 지연, 장애 확산, 성능 병목.
  - 이유: 대규모 트래픽과 도메인별 확장성 요구로 배제.

## 6. 구현 계획
1. **Kafka 설정**:
   - 토픽 생성: `order-topic`, `payment-topic`, `point-topic`, `reservation-topic`, `reservation-confirmed-topic`.
   - DLQ 설정: 실패 이벤트 저장 및 재처리.
2. **서비스 개발**:
   - 각 도메인 서비스에 Kafka Producer/Consumer 추가.
   - 로컬 트랜잭션(`@Transactional`)으로 데이터 일관성 보장.
3. **모니터링**:
   - Prometheus/Grafana로 Kafka 및 서비스 상태 모니터링.
   - 이벤트 실패 로그 수집(Sentry, ELK).
4. **테스트**:
   - 단위 테스트: 각 서비스의 로컬 트랜잭션 테스트.
   - 통합 테스트: 사가 흐름 테스트(예: 결제 실패 시 롤백 확인).

## 7. 결론
콘서트 예약 시스템을 마이크로서비스 아키텍처로 전환하고, 코레오그래피 기반 사가 패턴을 사용하여 분산 트랜잭션 문제를 해결합니다. 이를 통해 도메인별 독립성, 장애 격리, 확장성을 확보하며, Kafka 이벤트를 활용해 데이터 일관성을 유지합니다. 추가로 모니터링 및 재시도 메커니즘을 도입하여 안정성을 강화합니다.

## 8. 추가 요청
- **필요 시**: 특정 서비스의 상세 코드(컨트롤러, 리포지토리 등) 제공.
- **Kafka 설정**: 상세한 토픽/파티션 구성 요청 시 제공.
- **모니터링**: Prometheus/Grafana 설정 예시 요청 시 제공.
- **테스트 코드**: 사가 흐름 테스트 케이스 요청 시 제공.
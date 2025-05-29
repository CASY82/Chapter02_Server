# Apache Kafka 도입 보고서

## 1. Kafka의 기본 개념

Apache Kafka는 대규모 실시간 데이터 스트리밍을 처리하기 위한 분산 메시징 시스템입니다. 높은 처리량, 확장성, 내구성을 제공하며, 주로 이벤트 드리븐 아키텍처에서 사용됩니다.

### 주요 개념
- **토픽(Topic)**: 메시지가 저장되는 논리적 단위입니다. 데이터는 토픽에 저장되며, 프로듀서가 메시지를 보내고 컨슈머가 이를 소비합니다.
- **파티션(Partition)**: 토픽은 여러 파티션으로 나뉘어 병렬 처리를 가능하게 합니다다. 각 파티션은 순서가 보장되는 메시지 로그입니다.
- **컨슈머 그룹(Consumer Group)**: 여러 컨슈머가 협력하여 토픽의 메시지를 분산 처리합니다다. 각 파티션은 하나의 컨슈머에만 할당됩니다.
- **오프셋(Offset)**: 파티션 내 메시지의 고유 식별자입니다다. 컨슈머는 오프셋을 추적하여 어디까지 읽었는지 관리합니다.
- **Concurrency 어노테이션**: Kafka 자체는 어노테이션을 통해 동시성을 직접 제어하지 않지만, Spring Kafka 같은 프레임워크에서 `@KafkaListener`를 사용하여 동시성을 설정할 수 있습니다.  
예를 들어, `concurrency` 속성을 통해 컨슈머 스레드 수를 지정합니다:
  ```java
  @KafkaListener(topics = "my-topic", groupId = "my-group", concurrency = "3")
  public void consume(String message) {
      System.out.println("Received: " + message);
  }
  ```
  여기서 `concurrency = "3"`은 3개의 스레드가 병렬로 메시지를 처리하도록 설정됩니다.

### Producer, Partition, Consumer 수에 따른 데이터 흐름
Kafka의 데이터 흐름은 **Producer**, **Partition**, **Consumer**의 수에 따라 크게 영향을 받으며, 이는 처리량과 병렬 처리에 직접적인 영향을 미칩니다.

#### 1. **단일 Producer, 단일 Partition, 단일 Consumer**
- **흐름**: 한 명의 프로듀서가 하나의 파티션에 메시지를 전송하고, 한 명의 컨슈머가 해당 파티션에서 메시지를 소비합니다.
- **특징**: 
  - 메시지 순서 보장.
  - 처리량이 제한적(단일 파티션과 컨슈머로 인해 병렬성 없음).
- **사용 사례**: 순서가 중요한 단순한 워크로드(예: 로그 수집).
- **예시**:
  ```
  Producer -> [Topic: my-topic, Partition: 0] -> Consumer
  ```

#### 2. **단일 Producer, 다중 Partition, 단일 Consumer**
- **흐름**: 프로듀서가 여러 파티션에 메시지를 분배(기본적으로 키 기반 또는 라운드 로빈). 단일 컨슈머가 모든 파티션에서 메시지를 소비합니다.
- **특징**:
  - 단일 컨슈머가 모든 파티션을 처리하므로 병렬성 활용 불가.
  - 파티션 간 메시지 순서는 보장되지 않음.
- **사용 사례**: 낮은 소비율의 컨슈머가 데이터를 처리할 때.
- **예시**:
  ```
  Producer -> [Topic: my-topic, Partition: 0, 1, 2] -> Consumer
  ```

#### 3. **단일 Producer, 다중 Partition, 다중 Consumer (Consumer Group)**
- **흐름**: 프로듀서가 여러 파티션에 메시지를 분배. 컨슈머 그룹 내의 컨슈머들이 파티션을 나누어 소비합니다(각 파티션은 하나의 컨슈머에만 할당).
- **특징**:
  - 높은 병렬성과 처리량.
  - 컨슈머 수 > 파티션 수일 경우, 초과 컨슈머는 유휴 상태.
  - 파티션 내 순서 보장, 파티션 간 순서 미보장.
- **사용 사례**: 대규모 데이터 처리(예: 실시간 분석).
- **예시**:
  ```
  Producer -> [Topic: my-topic, Partition: 0] -> Consumer 1
                     [Partition: 1] -> Consumer 2
                     [Partition: 2] -> Consumer 3
  ```

#### 4. **다중 Producer, 다중 Partition, 다중 Consumer**
- **흐름**: 여러 프로듀서가 동일 토픽의 여러 파티션에 메시지를 전송. 컨슈머 그룹이 파티션을 나누어 소비합니다.
- **특징**:
  - 최대 병렬성과 처리량.
  - 파티션 키를 사용해 특정 데이터가 동일 파티션으로 전송되도록 보장 가능.
  - 복잡한 설정과 모니터링 필요.
- **사용 사례**: 초고속 데이터 스트리밍(예: 실시간 추천 시스템).
- **예시**:
  ```
  Producer 1 -> [Topic: my-topic, Partition: 0] -> Consumer 1
  Producer 2 -> [Partition: 1] -> Consumer 2
  Producer 3 -> [Partition: 2] -> Consumer 3
  ```

#### 데이터 흐름 최적화 팁
- **파티션 수**: 처리량에 따라 적절히 설정(너무 많으면 오버헤드 발생).
- **컨슈머 수**: 파티션 수와 맞추어 스케일링.
- **메시지 키**: 동일 키의 메시지가 동일 파티션으로 라우팅되도록 설정하여 순서 보장.
- **부하 분산**: 프로듀서가 라운드 로빈 또는 키 기반으로 메시지를 분배하여 파티션 간 균형 유지.

---


### Kafka의 아키텍처
- **프로듀서(Producer)**: 메시지를 토픽에 전송.
- **브로커(Broker)**: Kafka 서버로, 메시지를 저장하고 관리.
- **컨슈머(Consumer)**: 토픽에서 메시지를 읽어 처리.
- **주키퍼(Zookeeper)** 또는 **KRaft**: 클러스터 메타데이터 관리. 최신 Kafka는 KRaft(Kafka Raft)로 주키퍼 의존성을 제거.

---

## 2. Docker Compose로 KRaft Kafka 실행

KRaft(Kafka Raft)는 주키퍼 없이 Kafka 클러스터를 관리하는 모드입니다. 아래는 Docker Compose를 사용해 단일 노드 KRaft Kafka를 설정하는 예시입니다.

### Docker Compose 설정
```yaml
version: '3.8'

services:
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      CLUSTER_ID: "z4mUZrg7ZVumUSY3lPfpNA"
      KAFKA_KRAFT_BROKER_ID: "1"
      KAFKA_NODE_ID: "1"
      KAFKA_PROCESS_ROLES: "broker,controller"
      KAFKA_CONTROLLER_LISTENER_NAMES: "CONTROLLER"
      KAFKA_LISTENERS: "PLAINTEXT://:9092,CONTROLLER://:9093"
      KAFKA_ADVERTISED_LISTENERS: "PLAINTEXT://localhost:9092"
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT"
      KAFKA_CONTROLLER_QUORUM_VOTERS: "1@kafka:9093"
      KAFKA_LOG_DIRS: "/var/lib/kafka/data"
    volumes:
      - kafka-data:/var/lib/kafka/data

volumes:
  kafka-data:
```

### 실행 방법
1. 위의 설정을 `docker-compose.yml` 파일로 저장.
2. 터미널에서 다음 명령어 실행:
   ```bash
   docker-compose up -d
   ```
3. Kafka가 실행 중인지 확인:
   ```bash
   docker ps
   ```
4. 토픽 생성 예시:
   ```bash
   docker exec -it kafka kafka-topics.sh --create --topic my-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
   ```

### 참고
- `CLUSTER_ID`는 고유해야 하며, `base64`로 인코딩된 16바이트 값을 사용해야 합니다.
- KRaft는 주키퍼 없이 자체적으로 메타데이터를 관리하므로 설정을을 간소화할 수 있습니다.

### 실행 화면
![Image](https://github.com/user-attachments/assets/c0aa489c-d525-40da-9cc6-a7299b95916a)

---

## 3. DLQ와 실패 전략 처리

### DLQ (Dead Letter Queue)
DLQ는 처리에 실패한 메시지를 저장하는 별도의 토픽입니다. 이를 통해 실패한 메시지를 재처리하거나 분석할 수 있습니다.

#### DLQ 설정 (Spring Kafka 예시)
```java
@KafkaListener(topics = "my-topic", groupId = "my-group", errorHandler = "kafkaErrorHandler")
public void consume(String message) {
    // 메시지 처리 로직
    if (someCondition) {
        throw new RuntimeException("Processing failed");
    }
}

@Bean
public KafkaListenerErrorHandler kafkaErrorHandler(DeadLetterPublishingRecoverer recoverer) {
    return (message, exception) -> {
        // DLQ로 메시지 전송
        recoverer.accept(message, exception);
        return null;
    };
}

@Bean
public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, String> template) {
    return new DeadLetterPublishingRecoverer(template, (record, ex) -> new TopicPartition("my-topic.DLQ", -1));
}
```

#### 실패 전략
- **재시도(Retry)**: Spring Kafka의 `@RetryableTopic`을 사용해 실패한 메시지를 재시도.
  ```java
  @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 1000))
  @KafkaListener(topics = "my-topic", groupId = "my-group")
  public void consume(String message) {
      // 처리 로직
  }
  ```
  3번 재시도 후 실패 시 DLQ로 전송.
- **오프셋 커밋 전략**: `AckMode.MANUAL`을 사용하여 메시지 처리 후 명시적으로 오프셋 커밋.
- **에러 로깅**: 실패 원인을 로깅하고, 모니터링 시스템(예: Prometheus)으로 전송.
- **지수 백오프(Exponential Backoff)**: 재시도 간격을 지수적으로 증가시켜 부하 완화.

---

## 4. Transactional Outbox Pattern vs CDC

### Transactional Outbox Pattern
- **설명**: 데이터베이스 트랜잭션 내에서 이벤트를 생성하고, 이를 별도의 테이블(Outbox)에 저장. 이후 별도의 프로세스가 Outbox 테이블을 폴링하여 Kafka로 이벤트를 전송.
- **장점**:
  - 데이터베이스와 이벤트 전송의 원자성 보장.
  - 기존 애플리케이션에 쉽게 통합 가능.
- **단점**:
  - 추가적인 폴링 프로세스 필요.
  - 지연 가능성 있음.
- **예시**:
  ```sql
  CREATE TABLE outbox (
      id UUID PRIMARY KEY,
      event_type VARCHAR(100),
      payload JSONB,
      created_at TIMESTAMP
  );
  ```
  애플리케이션이 데이터와 이벤트를 동일 트랜잭션으로 저장하고, 폴링 작업이 이를 Kafka로 전송.

### CDC (Change Data Capture)
- **설명**: 데이터베이스의 변경 사항(INSERT, UPDATE, DELETE)을 실시간으로 캡처하여 Kafka로 전송. Debezium 같은 도구 사용.
- **장점**:
  - 실시간성 뛰어남.
  - 데이터베이스 변경 사항 자동 캡처.
- **단점**:
  - 설정이 복잡할 수 있음.
  - 데이터베이스 로그에 의존.
- **예시**:
  Debezium으로 MySQL CDC 설정:
  ```json
  {
    "name": "mysql-connector",
    "config": {
      "connector.class": "io.debezium.connector.mysql.MySqlConnector",
      "database.hostname": "mysql",
      "database.port": "3306",
      "database.user": "user",
      "database.password": "password",
      "database.server.id": "1",
      "database.include.list": "my_db",
      "topic.prefix": "cdc"
    }
  }
  ```

### 차이점
| 항목                | Transactional Outbox                | CDC                              |
|---------------------|-------------------------------------|----------------------------------|
| **구현 방식**       | 애플리케이션 레벨에서 Outbox 테이블 사용 | 데이터베이스 로그 기반 캡처       |
| **실시간성**        | 폴링 주기에 따라 지연 발생 가능       | 로그 기반으로 거의 실시간        |
| **복잡성**          | 비교적 간단한 설정                   | 추가 도구(Debezium 등) 필요      |
| **원자성**          | 트랜잭션 내 이벤트 저장으로 보장      | DB 로그에 의존하므로 별도 관리 필요 |
| **사용 사례**       | 트랜잭션 일관성 중요한 경우           | 대규모 데이터 변경 추적 필요 시   |

---


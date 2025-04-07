package kr.hhplus.be.server.domain.order;

import java.time.Instant;

public class Order {
	private Long id; // DB 식별자
	private Long orderId; // 도메인 식별자
	private Long userRefId;
	private Long paymentRefId;
	private Integer totalAmount;
	private String orderStatus;
	private Instant orderDate;
	private Instant createdAt;
	private Instant updatedAt;
}

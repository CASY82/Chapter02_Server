package kr.hhplus.be.server.domain.orderitem;

import java.time.Instant;

public class OrderItem {
	private Long id;
	private Long orderId;
	private Long userRefId;
	private Long paymentRefId;
	private Instant orderDate;
	private Integer totalAmount;
	private String orderStatus;
	private Instant createdAt;
	private Instant updatedAt;
}

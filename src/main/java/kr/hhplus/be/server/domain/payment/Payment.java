package kr.hhplus.be.server.domain.payment;

import java.time.Instant;

public class Payment {
	private Long id;
	private Long paymentId;
	private Long userRefId;
	private Instant paymentDate;
	private Integer amount;
	private String paymentStatus;
	private Instant createdAt;
	private Instant updatedAt;
}

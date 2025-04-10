package kr.hhplus.be.server.domain.payment;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

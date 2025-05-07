package kr.hhplus.be.server.application.obj;

import lombok.Data;

@Data
public class PaymentResult {
	private String paymentStatus;
    private Long remainPoint;
}

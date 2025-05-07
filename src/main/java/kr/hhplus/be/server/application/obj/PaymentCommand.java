package kr.hhplus.be.server.application.obj;

import lombok.Data;

@Data
public class PaymentCommand {
	private String userId;
	private Long reservationId;
}

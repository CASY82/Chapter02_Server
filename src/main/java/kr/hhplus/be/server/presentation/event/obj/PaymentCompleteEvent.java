package kr.hhplus.be.server.presentation.event.obj;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCompleteEvent {
	private Long paymentId;
	private Long reservationId;
	private Long userId;
}

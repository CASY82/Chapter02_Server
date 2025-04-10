package kr.hhplus.be.server.presentation.api.v1.obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
	Long userId;
	int amount;
}

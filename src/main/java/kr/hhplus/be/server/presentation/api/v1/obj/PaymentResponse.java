package kr.hhplus.be.server.presentation.api.v1.obj;

import lombok.Data;

@Data
public class PaymentResponse {
    private String paymentStatus;
    private Long remainPoint;
}
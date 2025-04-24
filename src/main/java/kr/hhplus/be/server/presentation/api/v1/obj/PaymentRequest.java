package kr.hhplus.be.server.presentation.api.v1.obj;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class PaymentRequest {
    @NotBlank(message = "User ID must not be blank")
    private String userId;

    @NotNull(message = "Reservation ID must not be null")
    private Long reservationId;
}
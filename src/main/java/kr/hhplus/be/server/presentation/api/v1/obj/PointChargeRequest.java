package kr.hhplus.be.server.presentation.api.v1.obj;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Data
public class PointChargeRequest {
    @NotBlank(message = "User ID must not be blank")
    private String userId;

    @Positive(message = "Amount must be positive")
    private Long amount;
}
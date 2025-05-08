package kr.hhplus.be.server.application.obj;

import java.util.List;

import lombok.Data;

@Data
public class ReserveResult {
    private Long reservationId;
    private String status;
    private List<Long> seatIds;
}

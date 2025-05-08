package kr.hhplus.be.server.application.obj;

import lombok.Data;

import java.util.List;

@Data
public class ReserveCommand {
    // 스케쥴과 좌석을 입력 받아야 함
    private Long scheduleId;
    private List<Long> seatId;
    private String userId;
    private Integer price;
    private Long orderId;
}

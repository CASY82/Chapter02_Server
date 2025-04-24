package kr.hhplus.be.server.application.obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationItemCommand {
    private Long itemId;
    private Integer quantity;
    private Integer unitPrice;
}

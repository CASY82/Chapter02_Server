package kr.hhplus.be.server.domain.orderitem;

import java.util.List;

public interface OrderItemRepository {
    List<OrderItem> findByOrderRefId(Long orderRefId);
}

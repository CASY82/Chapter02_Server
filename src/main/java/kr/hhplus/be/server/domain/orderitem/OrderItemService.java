package kr.hhplus.be.server.domain.orderitem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> getOrderItems(Long orderRefId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderRefId(orderRefId);
        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("No order items found for orderRefId: " + orderRefId);
        }
        return orderItems;
    }

    public int calculateTotalAmount(Long orderRefId) {
        List<OrderItem> orderItems = getOrderItems(orderRefId);
        return orderItems.stream()
                .mapToInt(item -> item.getQuantity() * item.getPrice())
                .sum();
    }
}

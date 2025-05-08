package kr.hhplus.be.server.domain.order;


import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.orderitem.OrderItemService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;

    public Order getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with orderId: " + orderId));
        int totalAmount = orderItemService.calculateTotalAmount(orderId);
        if (!order.getTotalAmount().equals(totalAmount)) {
            throw new IllegalArgumentException("Order total amount does not match order items total");
        }
        return order;
    }

    public Order updatePaymentRefId(Long orderId, Long paymentId) {
        Order order = getOrder(orderId);
        order.setPaymentRefId(paymentId);
        order.setOrderStatus("PAID");
        return orderRepository.save(order);
    }
    
    public void save(Order obj) {
    	this.orderRepository.save(obj);
    }
}
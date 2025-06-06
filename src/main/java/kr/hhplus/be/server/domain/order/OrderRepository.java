package kr.hhplus.be.server.domain.order;

import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long orderId);
    Optional<Order> findByIdWithLock(Long orderId);
    Order save(Order order);
}
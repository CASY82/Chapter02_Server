package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderDomainRepository implements OrderRepository {

    private final OrderJpaRepository repository;

    @Override
    public Optional<Order> findById(Long orderId) {
        return repository.findById(orderId);
    }

    @Override
    public Order save(Order order) {
        return repository.save(order);
    }
}

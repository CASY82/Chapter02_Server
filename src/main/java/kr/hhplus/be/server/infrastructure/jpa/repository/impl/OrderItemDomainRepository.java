package kr.hhplus.be.server.infrastructure.jpa.repository.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.orderitem.OrderItem;
import kr.hhplus.be.server.domain.orderitem.OrderItemRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.OrderItemJpaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemDomainRepository implements OrderItemRepository {

    private final OrderItemJpaRepository repository;

    @Override
    public List<OrderItem> findByOrderRefId(Long orderRefId) {
        return repository.findByOrder_OrderId(orderRefId);
    }
}
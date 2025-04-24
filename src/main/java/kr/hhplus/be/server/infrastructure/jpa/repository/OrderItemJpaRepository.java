package kr.hhplus.be.server.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.domain.orderitem.OrderItem;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderRefId(Long orderRefId);
}
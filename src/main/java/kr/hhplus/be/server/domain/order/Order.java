package kr.hhplus.be.server.domain.order;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order")
public class Order extends BaseEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DB 식별자

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId; // 도메인 식별자

    @Column(name = "user_ref_id", nullable = false)
    private Long userRefId;

    @Column(name = "payment_ref_id", nullable = false)
    private Long paymentRefId;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "order_status", nullable = false)
    private String orderStatus;

    @Column(name = "order_date", nullable = false)
    private Instant orderDate;
}

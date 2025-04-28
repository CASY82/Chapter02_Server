package kr.hhplus.be.server.domain.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
	Optional<Payment> findByPaymentId(Long paymentId);
    List<Payment> findByUserRefIdWithLock(Long userRefId);
    List<Payment> findAll();
    Payment save(Payment payment);
}

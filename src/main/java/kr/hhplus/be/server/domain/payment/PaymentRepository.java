package kr.hhplus.be.server.domain.payment;

import java.util.List;

public interface PaymentRepository {
	void save(Payment payment);
	Payment findByUserRefId(Long userRefId);
	List<Payment> findAll();
}

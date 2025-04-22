package kr.hhplus.be.server.domain.payment;

public interface PaymentRepository {
	void save(Payment payment);
	Payment findByUserRefId(Long userRefId);
}

package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentDomainRepository implements PaymentRepository {

    private final PaymentJpaRepository repository;

    @Override
    public Optional<Payment> findByPaymentId(Long paymentId) {
        return repository.findByPaymentId(paymentId);
    }

    @Override
    public List<Payment> findByUserRefIdWithLock(Long userRefId) {
        return repository.findByUserRefIdWithLock(userRefId);
    }
    
    @Override
    public Payment save(Payment payment) {
        return repository.save(payment);
    }

	@Override
	public List<Payment> findAll() {
		return repository.findAll();
	}
}

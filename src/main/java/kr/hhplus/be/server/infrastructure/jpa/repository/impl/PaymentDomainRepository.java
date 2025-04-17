package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentDomainRepository implements PaymentRepository {
	
	private final PaymentJpaRepository repository;

	@Override
	public void save(Payment payment) {
		this.repository.save(payment);
	}

	@Override
	public Payment findByUserRefId(Long userRefId) {
		return this.findByUserRefId(userRefId);
	}

	@Override
	public List<Payment> findAll() {
		return this.repository.findAll();
	}
}

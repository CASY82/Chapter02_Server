package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

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

}

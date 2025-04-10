package kr.hhplus.be.server.domain.payment;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	
	private final PaymentRepository repository;
	
	public void save(Payment payment) {
		this.repository.save(payment);
	}
}

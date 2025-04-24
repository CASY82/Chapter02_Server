package kr.hhplus.be.server.domain.payment;

import java.time.Instant;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	
	private final PaymentRepository repository;
	
	public void save(Payment payment) {
		this.repository.save(payment);
	}
	
    public Payment createPayment(Long userRefId, Integer amount, Long paymentId) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setUserRefId(userRefId);
        payment.setPaymentDate(Instant.now());
        payment.setAmount(amount);
        payment.setPaymentStatus("OK");
        return this.repository.save(payment);
    }
}

package kr.hhplus.be.server.domain.payment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
	
	private final PaymentRepository paymentRepository;
	
	@Transactional
    public Payment processPayment(Long userRefId, Integer amount) {
        // 동일 사용자 결제 확인, 비관적 락 적용
        List<Payment> existingPayments = paymentRepository.findByUserRefIdWithLock(userRefId);
        for (Payment existingPayment : existingPayments) {
            if ("COMPLETED".equals(existingPayment.getPaymentStatus()) &&
                existingPayment.getAmount().equals(amount)) {
                throw new IllegalStateException("Payment already completed for user: " + userRefId);
            }
        }

        // 결제 생성
        Payment payment = new Payment();
        payment.setPaymentId(generateUniquePaymentId());
        payment.setUserRefId(userRefId);
        payment.setPaymentDate(Instant.now());
        payment.setAmount(amount);
        payment.setPaymentStatus("COMPLETED");

        return paymentRepository.save(payment);
    }

    private Long generateUniquePaymentId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
	
	public void save(Payment payment) {
		this.paymentRepository.save(payment);
	}
	
    public Payment createPayment(Long userRefId, Integer amount, Long paymentId) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setUserRefId(userRefId);
        payment.setPaymentDate(Instant.now());
        payment.setAmount(amount);
        payment.setPaymentStatus("OK");
        return this.paymentRepository.save(payment);
    }
}

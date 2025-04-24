package kr.hhplus.be.server.presentation.scheduler;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seatreservation.SeatReservation;
import kr.hhplus.be.server.domain.seatreservation.SeatReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatStateScheduler {

    private final ReservationRepository reservationRepository;
    private final SeatReservationRepository seatReservationRepository;

    /**
     * 5분마다 실행 (예: 300_000ms = 5분)
     */
    @Scheduled(fixedDelay = 300_000)
    @Transactional
    public void cancelExpiredPendingReservations() {
        log.info("[Scheduler] 시작: 5분 이상 경과한 PENDING 예약 취소 작업");

        // 5분 전에 생성된 좌석 예약 중 reserved = true 인 경우를 필터링
        List<SeatReservation> seatReservations = seatReservationRepository.findByScheduleIdAndNotCancelled(); // 실제 사용 시 조건 필요

        for (SeatReservation seatReservation : seatReservations) {
            if (!Boolean.TRUE.equals(seatReservation.getReserved())) {
                continue;
            }

            LocalDateTime reservedAt = seatReservation.getReservedAt();
            if (reservedAt == null || reservedAt.isAfter(LocalDateTime.now().minusMinutes(5))) {
                continue;
            }

            Long reservationRefId = seatReservation.getReservationRefId();

            Optional<Reservation> optionalReservation = reservationRepository.findByReservationIdWithLock(reservationRefId);
            if (optionalReservation.isEmpty()) {
                log.warn("예약 ID {} 에 해당하는 예약을 찾을 수 없음", reservationRefId);
                continue;
            }

            Reservation reservation = optionalReservation.get();

            // PENDING 상태만 취소 가능
            if (reservation.getReserveStatus() == ReservationStatus.PAY) {
                log.info("예약 ID {} 상태가 PAY -> CANCEL 처리", reservationRefId);
                reservation.cancel();
                reservationRepository.save(reservation);

                seatReservation.setReserved(false);
                seatReservation.setReservedAt(null); // 해제 시간이 필요 없다면 null 처리
                log.info("좌석 예약 ID {} 해제 완료", seatReservation.getId());
            } else {
                log.info("예약 ID {} 는 PAY 상태 아님. 현재 상태: {}", reservationRefId, reservation.getReserveStatus());
            }
        }

        log.info("[Scheduler] 종료: PENDING 예약 취소 및 좌석 해제 완료");
    }
}


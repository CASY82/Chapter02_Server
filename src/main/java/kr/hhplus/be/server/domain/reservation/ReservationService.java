package kr.hhplus.be.server.domain.reservation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.application.obj.ReserveCommand;
import kr.hhplus.be.server.domain.reservationitem.ReservationItem;
import kr.hhplus.be.server.domain.seatreservation.SeatReservation;
import kr.hhplus.be.server.domain.seatreservation.SeatReservationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatReservationRepository seatReservationRepository;

    @Transactional
    public Reservation createReservation(ReserveCommand command, Long userId) {
        // 예약 아이템 생성
        List<ReservationItem> items = new ArrayList<>();
        for (Long seatId : command.getSeatIds()) {
            ReservationItem item = new ReservationItem();
            item.setScheduleRefId(command.getScheduleId());
            item.setSeatRefId(seatId);
            item.setUnitPrice(command.getPrice());
            items.add(item);
        }

        // 예약 생성
        Reservation reservation = new Reservation();
        reservation.setUserRefId(userId);
        reservation.setScheduleRefId(command.getScheduleId());
        reservation.setOrderRefId(command.getOrderId());
        reservation.setReservationItems(items);
        reservation.setReserveStatus(ReservationStatus.READY);
        reservationRepository.save(reservation);

        // 좌석 예약 상태 업데이트
        for (Long seatId : command.getSeatIds()) {
            SeatReservation seatReservation = new SeatReservation();
            seatReservation.setSeatRefId(seatId);
            seatReservation.setReservationRefId(reservation.getReservationId());
            seatReservation.setReserved(true);
            seatReservation.setReservedAt(Instant.now());
            seatReservationRepository.save(seatReservation);
        }

        return reservation;
    }

    @Transactional
    public void completeReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
        reservation.setReserveStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);
    }    
    
    public Reservation getReservation(Long reservationId) {
    	return reservationRepository.findByReservationId(reservationId).orElseThrow();
    }
    
    public void save(Reservation reservation) {
    	reservationRepository.save(reservation);
    }
}
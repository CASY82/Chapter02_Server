package kr.hhplus.be.server.infrastructure.jpa.repository.impl;


import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.domain.seatreservation.SeatReservation;
import kr.hhplus.be.server.domain.seatreservation.SeatReservationRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.SeatReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatReservationDomainRepository implements SeatReservationRepository {

    private final SeatReservationJpaRepository repository;

    @Override
    public List<SeatReservation> findByScheduleIdAndNotCancelled() {
        return repository.findCancleSeatReservations(ReservationStatus.CANCEL);
    }
}
package kr.hhplus.be.server.domain.seatreservation;

import java.util.List;

public interface SeatReservationRepository {
    List<SeatReservation> findByScheduleIdAndNotCancelled();
}
package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seatreservation.SeatReservation;
import kr.hhplus.be.server.domain.seatreservation.SeatReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SeatReservationFacade {
    private final SeatService seatService;
    private final SeatReservationRepository seatReservationRepository;
    private final ScheduleService scheduleService;

    public List<Long> getAvailableSeatIds(Long scheduleId) {
        // 일정 확인 및 공연장 ID 조회
        Schedule schedule = scheduleService.getSchedule(scheduleId);
        Long venueRefId = schedule.getVenueRefId();

        // 공연장의 전체 좌석 조회
        List<Seat> seats = seatService.getSeatsByVenue(venueRefId);
        List<Long> seatIds = seats.stream()
                .map(Seat::getSeatId)
                .collect(Collectors.toList());

        // 예약된 좌석 조회 (reservation_status != CANCELLED)
        List<SeatReservation> reservedSeats = seatReservationRepository.findByScheduleIdAndNotCancelled();
        List<Long> reservedSeatIds = reservedSeats.stream()
                .map(SeatReservation::getSeatRefId)
                .collect(Collectors.toList());

        // 가용 좌석 필터링
        return seatIds.stream()
                .filter(seatId -> !reservedSeatIds.contains(seatId))
                .collect(Collectors.toList());
    }
}

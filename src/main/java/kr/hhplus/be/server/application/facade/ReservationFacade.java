package kr.hhplus.be.server.application.facade;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.obj.ReservationCheckCommand;
import kr.hhplus.be.server.application.obj.ReservationCheckResult;
import kr.hhplus.be.server.application.obj.ReserveCommand;
import kr.hhplus.be.server.application.obj.ReserveResult;
import kr.hhplus.be.server.domain.performance.Performance;
import kr.hhplus.be.server.domain.performance.PerformanceService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seatreservation.SeatReservation;
import kr.hhplus.be.server.domain.seatreservation.SeatReservationRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.lock.DistributedLock;
import kr.hhplus.be.server.infrastructure.queue.obj.ReservationEvent;
import kr.hhplus.be.server.presentation.event.obj.SeatReservedEvent;
import lombok.RequiredArgsConstructor;

/**
 * 예약 파사드
 * 1. 예약 가능 날짜 확인
 * 2. 예약 진행
 */
@Component
@RequiredArgsConstructor
public class ReservationFacade {
    private final PerformanceService performanceService;
    private final ScheduleService scheduleService;
    private final SeatService seatService;
    private final SeatReservationRepository seatReservationRepository;
    private final ReservationService reservationService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public ReservationCheckResult getAvailableSchedules(ReservationCheckCommand command) {
        ReservationCheckResult result = new ReservationCheckResult();
        Performance performance = performanceService.getPerformance(command.getPerformanceId());
        if (Objects.isNull(performance)) {
            throw new IllegalArgumentException("공연을 찾을 수 없습니다.");
        }
        result.setScheduleList(scheduleService.getAvailableSchedules(command.getPerformanceId()));
        return result;
    }

    public ReservationCheckResult getAvailableSeatIds(ReservationCheckCommand command) {
        ReservationCheckResult result = new ReservationCheckResult();
        
        // 일정 확인 및 공연장 ID 조회
        Schedule schedule = scheduleService.getSchedule(command.getScheduleId());
        Long venueRefId = schedule.getVenueRefId();

        // 공연장의 전체 좌석 조회
        List<Seat> seats = seatService.getSeatsByVenue(venueRefId);
        List<Long> seatIds = seats.stream()
                .map(Seat::getSeatId)
                .collect(Collectors.toList());

        // 예약된 좌석 및 5분간 점유된 좌석 확인
        List<SeatReservation> reservedSeats = seatReservationRepository.findByScheduleIdAndNotCancelled();
        List<Long> reservedSeatIds = reservedSeats.stream()
                .map(SeatReservation::getSeatRefId)
                .collect(Collectors.toList());

        // 가용 좌석 필터링
        result.setSeatIds(seatIds.stream()
                .filter(seatId -> !reservedSeatIds.contains(seatId))
                .collect(Collectors.toList()));
        
        return result;
    }

    @Transactional
    @DistributedLock(key = "'reserveLock:' + #command.scheduleId", waitTime = 5, leaseTime = 3)
    public ReserveResult reserve(ReserveCommand command) {
        // 유저 검증
        User user = userService.getUser(command.getUserId());

        // 좌석 가용성 확인
        List<Seat> seats = seatService.getSeatsByVenue(scheduleService.getSchedule(command.getScheduleId()).getVenueRefId());
        List<Long> reservedSeatIds = seatReservationRepository.findByScheduleIdAndNotCancelled()
                .stream()
                .map(SeatReservation::getSeatRefId)
                .toList();

        boolean allSeatsAvailable = command.getSeatIds().stream()
                .allMatch(seatId -> !reservedSeatIds.contains(seatId) &&
                        seats.stream().anyMatch(seat -> seat.getSeatId().equals(seatId)));

        if (!allSeatsAvailable) {
            throw new IllegalStateException("선택한 좌석 중 일부가 이미 예약되었습니다.");
        }

        // 예약 생성
        Reservation reservation = reservationService.createReservation(command, user.getId());

        // 이벤트 발행
        eventPublisher.publishEvent(new SeatReservedEvent(
                reservation.getReservationId(), command.getScheduleId(), command.getSeatIds()));
        eventPublisher.publishEvent(new ReservationEvent(
                scheduleService.getSchedule(command.getScheduleId()).getPerformanceRefId(),
                command.getScheduleId(), (long) seats.size(), (long) reservedSeatIds.size() + command.getSeatIds().size()));

        ReserveResult result = new ReserveResult();
        result.setReservationId(reservation.getReservationId());
        result.setStatus(reservation.getReserveStatus().name());
        result.setSeatIds(command.getSeatIds());
        return result;
    }
}
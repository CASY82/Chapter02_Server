package kr.hhplus.be.server.application.facade;

import kr.hhplus.be.server.application.obj.ReservationCheckCommand;
import kr.hhplus.be.server.application.obj.ReservationCheckResult;
import kr.hhplus.be.server.application.obj.ReserveCommand;
import kr.hhplus.be.server.application.obj.ReserveResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.performance.Performance;
import kr.hhplus.be.server.domain.performance.PerformanceService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservationitem.ReservationItem;
import kr.hhplus.be.server.domain.schedule.Schedule;
import kr.hhplus.be.server.domain.schedule.ScheduleService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seatreservation.SeatReservation;
import kr.hhplus.be.server.domain.seatreservation.SeatReservationRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infrastructure.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public ReservationCheckResult getAvailableSchedules(ReservationCheckCommand command) {
        ReservationCheckResult result = new ReservationCheckResult();

        // 공연 존재 여부 확인
        Performance performance = performanceService.getPerformance(command.getPerformanceId());

        if(Objects.isNull(performance)) {
            throw new IllegalArgumentException("Can't found Performance");
        }

        result.setScheduleList(this.scheduleService.getAvailableSchedules(command.getPerformanceId()));

        // 예약 가능 일정 조회
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
    @DistributedLock(key = "reserveLock", waitTime = 5, leaseTime = 3)
    public ReserveResult reserve(ReserveCommand command) {
		ReserveResult result = new ReserveResult();

        User user = userService.getUser(command.getUserId());
        
        // 1. 스케줄 검증
        Schedule schedule = scheduleService.getSchedule(command.getScheduleId());
        if (Objects.isNull(schedule)) {
            throw new IllegalArgumentException("스케줄을 찾을 수 없습니다");
        }

        // 2. 좌석 가용성 확인
        List<Seat> seats = seatService.getSeatsByVenue(schedule.getVenueRefId());
        List<Long> reservedSeatIds = seatReservationRepository.findByScheduleIdAndNotCancelled()
            .stream()
            .map(SeatReservation::getSeatRefId)
            .toList();

        boolean allSeatsAvailable = command.getSeatId().stream()
            .allMatch(seatId -> !reservedSeatIds.contains(seatId) && 
                              seats.stream().anyMatch(seat -> seat.getSeatId().equals(seatId)));
        
        if (!allSeatsAvailable) {
            throw new IllegalStateException("선택한 좌석 중 일부가 이미 예약되었습니다");
        }

        // 3. 예약 아이템 생성
        List<ReservationItem> items = new ArrayList<>();
        for (Long seatId : command.getSeatId()) {
            ReservationItem item = new ReservationItem();
            item.setScheduleRefId(command.getScheduleId());
            item.setSeatRefId(seatId);
            item.setUnitPrice(command.getPrice()); // 일단 임시로 화면단에서 가격을 받아온다 가정
            items.add(item);
        }

        // 4. 예약 생성
        Reservation reservation = reservationService.createReservation(
            user.getId(),
            command.getScheduleId(),
            command.getOrderId(),
            items
        );

        // 5. 좌석 예약 상태 업데이트(락 필요)
        // Service쪽으로 걷어낼 예정
        for (Long seatId : command.getSeatId()) {
            SeatReservation seatReservation = new SeatReservation();
            seatReservation.setSeatRefId(seatId);
            seatReservation.setReservationRefId(reservation.getReservationId());
            seatReservation.setReserved(true);
            seatReservation.setReservedAt(Instant.now());
            seatReservationRepository.save(seatReservation);
        }

        // 6. 예약 완료
        reservationService.completeReservation(reservation.getReservationId());

        result.setReservationId(reservation.getReservationId());
        result.setStatus(reservation.getReserveStatus().name());
        result.setSeatIds(command.getSeatId());

        return result;
		
	}
}


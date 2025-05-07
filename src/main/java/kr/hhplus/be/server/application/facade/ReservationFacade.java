package kr.hhplus.be.server.application.facade;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.application.obj.ReservationCheckCommand;
import kr.hhplus.be.server.application.obj.ReservationCheckResult;
import kr.hhplus.be.server.application.obj.ReserveCommand;
import kr.hhplus.be.server.application.obj.ReserveResult;
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
	
	public ReserveResult reserve(ReserveCommand command) {
		ReserveResult result = new ReserveResult();
        
        // 1. 스케줄 검증
        Schedule schedule = scheduleService.getSchedule(command.getScheduleId());
        if (Objects.isNull(schedule)) {
            throw new IllegalArgumentException("스케줄을 찾을 수 없습니다");
        }

        // 2. 좌석 가용성 확인
        List<Seat> seats = seatService.getSeatsByVenue(schedule.getVenueRefId());
        List<Long> requestedSeatIds = command.getSeatIds();
        List<Long> reservedSeatIds = seatReservationRepository.findByScheduleIdAndNotCancelled()
            .stream()
            .map(SeatReservation::getSeatRefId)
            .toList();

        boolean allSeatsAvailable = requestedSeatIds.stream()
            .allMatch(seatId -> !reservedSeatIds.contains(seatId) && 
                              seats.stream().anyMatch(seat -> seat.getSeatId().equals(seatId)));
        
        if (!allSeatsAvailable) {
            throw new IllegalStateException("선택한 좌석 중 일부가 이미 예약되었습니다");
        }

        // 3. 예약 아이템 생성
        List<ReservationItem> items = new ArrayList<>();
        for (Long seatId : requestedSeatIds) {
            ReservationItem item = new ReservationItem();
            item.setQuantity(1);
            item.setUnitPrice(10000); // 예시 가격, 실제로는 공연/좌석별 가격 설정 필요
            items.add(item);
        }

        // 4. 예약 생성
        Reservation reservation = reservationService.createReservationWithItems(
            command.getUserId(),
            requestedSeatIds.get(0), // 단일 좌석 예약 기준, 다중 좌석은 별도 처리 필요
            command.getScheduleId(),
            generateOrderId(),
            items
        );

        // 5. 좌석 예약 상태 업데이트
        for (Long seatId : requestedSeatIds) {
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
        result.setSeatIds(requestedSeatIds);

        return result;
		
	}
}


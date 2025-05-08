package kr.hhplus.be.server.domain.seatreservation;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatReservationService {

    private final SeatReservationRepository seatReservationRepository;

    public void updateStatus() {

    }
}

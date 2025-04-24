package kr.hhplus.be.server.domain.seat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    public List<Seat> getSeatsByVenue(Long venueRefId) {
        return seatRepository.findByVenueRefId(venueRefId);
    }
}
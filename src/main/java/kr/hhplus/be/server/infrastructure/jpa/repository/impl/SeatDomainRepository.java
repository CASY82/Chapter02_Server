package kr.hhplus.be.server.infrastructure.jpa.repository.impl;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.infrastructure.jpa.repository.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatDomainRepository implements SeatRepository {

    private final SeatJpaRepository repository;

    @Override
    public List<Seat> findByVenueRefId(Long venueRefId) {
        return repository.findByVenueRefId(venueRefId);
    }

	@Override
	public void save(Seat seat) {
		repository.save(seat);
	}
}
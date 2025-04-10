package kr.hhplus.be.server.domain.reservationitem;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationItemService {
	
	private final ReservationItemRepository repository;

}

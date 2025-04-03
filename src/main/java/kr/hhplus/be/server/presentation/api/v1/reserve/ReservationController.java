package kr.hhplus.be.server.presentation.api.v1.reserve;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {
	
	@GetMapping("/reservations/available/seat")
	public ResponseEntity<List<Integer>> getAvailableSeat(@RequestParam("venueId") int venueId) {
		List<Integer> seatList = new ArrayList<>();
		
		seatList.add(1);
		seatList.add(2);
		seatList.add(3);
		seatList.add(4);
		seatList.add(5);
		
		return ResponseEntity.ok(seatList);
	}
	
	@GetMapping("/reservations/available/schedule")
	public ResponseEntity<List<Integer>> getAvailableSchedule(@RequestParam("performanceId") int performanceId) {
		List<Integer> scheduleList = new ArrayList<>();
		
		scheduleList.add(1);
		scheduleList.add(2);
		scheduleList.add(3);
		scheduleList.add(4);
		scheduleList.add(5);
		
		return ResponseEntity.ok(scheduleList);
	}
	
	@PostMapping("/reservations")
	public ResponseEntity<Boolean> reserve(@RequestBody int performanceId) {
		return ResponseEntity.ok(true);
	}
	
}

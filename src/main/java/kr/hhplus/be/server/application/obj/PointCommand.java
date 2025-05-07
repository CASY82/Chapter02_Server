package kr.hhplus.be.server.application.obj;

import lombok.Data;

@Data
public class PointCommand {
	private String userId;
	private Long amount;
}

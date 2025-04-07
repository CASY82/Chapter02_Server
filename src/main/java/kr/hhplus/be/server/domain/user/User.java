package kr.hhplus.be.server.domain.user;

import java.time.Instant;

public class User {
	private Long id; // 테이블 id
	private String userId; // 식별자(별도 클래스로 관리 예정)
	private String username;
	private String password;
	private Instant createdAt;
	private Instant updatedAt;
}

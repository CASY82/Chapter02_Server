package kr.hhplus.be.server.presentation.api.v1.token.obj;

import java.time.Instant;

public class AuthToken {
	private String token;
	private Instant expirationDate;
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void setExpirationDate(Instant expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getToken() {
		return token;
	}

	public Instant getExpirationDate() {
		return expirationDate;
	}
}

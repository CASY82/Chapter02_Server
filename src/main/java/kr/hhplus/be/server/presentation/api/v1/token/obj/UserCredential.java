package kr.hhplus.be.server.presentation.api.v1.token.obj;

public class UserCredential {
	private String userId;
	private String password;

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}
}

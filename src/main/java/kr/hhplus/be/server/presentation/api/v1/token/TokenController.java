package kr.hhplus.be.server.presentation.api.v1.token;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.hhplus.be.server.presentation.api.v1.token.obj.AuthToken;
import kr.hhplus.be.server.presentation.api.v1.token.obj.UserCredential;


@RestController
public class TokenController {
	
	@PostMapping("/auth/token")
	public ResponseEntity<AuthToken> publishToken(@RequestBody UserCredential user) {
		AuthToken token = new AuthToken();
		
		token.setExpirationDate(Instant.now());
		token.setToken("abcdefg");
		
		return ResponseEntity.ok(token);
	}
}

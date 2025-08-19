package com.cdbd.opensource.presentation.api.v1.account;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cdbd.opensource.application.account.AccountService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {
	
	private final AccountService service;
	
	@PostMapping("/account/change/password")
	public ResponseEntity<String> changePassword(@RequestBody AccountRequest req) {	
		this.service.changePassword(req.getId(), req.getPassword());
		
		return ResponseEntity.ok("Change Password Success");
	}
	
	@PostMapping("/account/change/auth")
	public ResponseEntity<String> changeAuth(@RequestBody AccountRequest req) {
		this.service.changeAuth(req.getId(), req.getAuth());
		
		return ResponseEntity.ok("Change Auth Success");
	}
	
	@Data
	public static class AccountRequest {
		private String id;
		private String password;
		private String auth;
	}
}

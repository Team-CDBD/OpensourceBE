package com.cdbd.opensource.application.account;

import org.springframework.stereotype.Service;

import com.cdbd.opensource.domain.account.Account;
import com.cdbd.opensource.domain.account.Auth;
import com.cdbd.opensource.domain.account.repository.AccountRepository;
import com.cdbd.opensource.infrastructure.encoder.PasswordEncoder;

import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	
	private final AccountRepository repository;
	private final PasswordEncoder passwordEncoder;
	
	// 로그인
	public boolean login(String id, String password) {
		Account checkAccount = this.repository.selectOneByAccountId(id);
		
		Assert.notNull(checkAccount, "Not available Account");
		
		return checkAccount.isPasswordMatch(password, this.passwordEncoder);
	}
	
	// 권한 변경
	public void changeAuth(String id, String newAuth) {
		Account account = this.repository.selectOneByAccountId(id);
		
		Assert.notNull(account, "Not available Account");
		
		account.assignRole(Auth.valueOf(newAuth));
		
		this.repository.save(account);
	}
	
	// 비밀번호 변경
	public void changePassword(String id, String newPassword) {
		Account account = this.repository.selectOneByAccountId(id);
		
		account.changePassword(newPassword, passwordEncoder);
		
		this.repository.save(account);
	}
}

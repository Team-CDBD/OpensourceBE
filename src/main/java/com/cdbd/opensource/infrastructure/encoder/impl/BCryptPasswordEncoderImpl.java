package com.cdbd.opensource.infrastructure.encoder.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.cdbd.opensource.infrastructure.encoder.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BCryptPasswordEncoderImpl implements PasswordEncoder {
	
	private final BCryptPasswordEncoder delegate;

	@Override
	public String encrypt(String plainText) {
		return delegate.encode(plainText);
	}

	@Override
	public String decrypt(String ciphertext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean matches(String plainText, String cipherText) {
		// TODO Auto-generated method stub
		return delegate.matches(plainText, cipherText);
	}

}

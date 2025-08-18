package com.cdbd.opensource.infrastructure.encoder;

public interface PasswordEncoder {
    String encrypt(String plainText);
    String decrypt(String ciphertext);
    boolean matches(String plainText, String cipherText);
}

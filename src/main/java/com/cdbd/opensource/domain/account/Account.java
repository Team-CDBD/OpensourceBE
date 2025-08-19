package com.cdbd.opensource.domain.account;

import com.cdbd.opensource.infrastructure.encoder.PasswordEncoder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    // DB 식별자
    private Long id;
    // 계정 Id
    private String accountId;
    // 비밀번호
    private String password;
    // 권한
    private Auth authorization;
    // 계정 생성 날짜
    private Instant createdAt;

    /**
     * 비밀번호 변경
     * @param newPassword
     */
    public void changePassword(String newPassword, PasswordEncoder encoder) {
        Assert.notNull(newPassword, "Password cannot be null");

        this.password = Objects.requireNonNull(encoder.encrypt(newPassword));
    }

    /**
     * 권한 변경
     * @param auth
     */
    public void assignRole(Auth auth) {
        this.authorization = auth;
    }

    /**
     * 패스워드 일치 여부 확인
     * @param rawPassword
     * @param encoder
     * @return
     */
    public boolean isPasswordMatch(String rawPassword, PasswordEncoder encoder) {
        Assert.notNull(rawPassword, "Password cannot be null");

        return encoder.matches(rawPassword, this.password);
    }
}

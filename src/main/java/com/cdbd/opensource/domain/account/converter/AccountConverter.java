package com.cdbd.opensource.domain.account.converter;

import com.cdbd.opensource.domain.account.Account;
import com.cdbd.opensource.infrastructure.jpa.entity.AccountEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AccountConverter {
	
	// AccountEntity -> Account 변환
    public Account toAccount(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getAccountId(),
                entity.getPassword(),
                entity.getAuthorization(),
                entity.getCreatedAt()
        );
    }

    // Account -> AccountEntity 변환
    public AccountEntity toEntity(Account account) {
    	if (account == null) {
            return null;
        }
        AccountEntity entity = new AccountEntity();
        entity.setId(account.getId());
        entity.setAccountId(account.getAccountId());
        entity.setPassword(account.getPassword());
        entity.setAuthorization(account.getAuthorization());
        // createdAt은 JPA Auditing(@CreatedDate)으로 설정되므로 Setter 호출하지 않음
        entity.setUpdatedAt(Instant.now()); // updatedAt은 현재 시간으로 설정
        return entity;
    }

}

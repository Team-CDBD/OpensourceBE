package com.cdbd.opensource.domain.account.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.cdbd.opensource.domain.account.Account;
import com.cdbd.opensource.domain.account.converter.AccountConverter;
import com.cdbd.opensource.domain.account.repository.AccountRepository;
import com.cdbd.opensource.infrastructure.jpa.entity.AccountEntity;
import com.cdbd.opensource.infrastructure.jpa.repo.AccountJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaAccountRepository implements AccountRepository {
	
	private final AccountJpaRepository accountJpaRepository;
    private final AccountConverter converter;

    @Override
    public Account selectOneById(Long id) {
        Optional<AccountEntity> entity = accountJpaRepository.findById(id);
        return entity.map(converter::toAccount).orElse(null);
    }

    @Override
    public Account selectOneByAccountId(String accountId) {
        Optional<AccountEntity> entity = accountJpaRepository.findByAccountId(accountId);
        return entity.map(converter::toAccount).orElse(null);
    }

    @Override
    public List<Account> selectAll() {
        return accountJpaRepository.findAll()
                .stream()
                .map(converter::toAccount)
                .collect(Collectors.toList());
    }

    @Override
    public Long save(Account account) {
        AccountEntity entity = converter.toEntity(account);
        AccountEntity savedEntity = accountJpaRepository.save(entity);
        return savedEntity.getId();
    }

}

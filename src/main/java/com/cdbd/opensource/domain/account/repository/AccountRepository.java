package com.cdbd.opensource.domain.account.repository;

import com.cdbd.opensource.domain.account.Account;

import java.util.List;

public interface AccountRepository {
    Account selectOneById(Long id);
    Account selectOneByAccountId(String accountId);
    List<Account> selectAll();
    Long save(Account account);
}

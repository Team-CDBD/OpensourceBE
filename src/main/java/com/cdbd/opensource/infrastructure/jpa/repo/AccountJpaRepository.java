package com.cdbd.opensource.infrastructure.jpa.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cdbd.opensource.infrastructure.jpa.entity.AccountEntity;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
	// ID로 계정 조회
    Optional<AccountEntity> findById(Long id);

    // accountId로 계정 조회
    Optional<AccountEntity> findByAccountId(String accountId);
}

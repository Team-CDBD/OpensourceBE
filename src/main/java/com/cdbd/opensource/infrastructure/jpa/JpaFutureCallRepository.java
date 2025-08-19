package com.cdbd.opensource.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaFutureCallRepository extends JpaRepository<FutureCallEntity, Long> {
    List<FutureCallEntity> findByEventLogId(Long id);
}

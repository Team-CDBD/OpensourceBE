package com.cdbd.opensource.infrastructure.jpa;


import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEventLogRepository extends JpaRepository<EventLogEntity, Long> {
}

package com.cdbd.opensource.infrastructure.jpa;


import com.cdbd.opensource.domain.eventlog.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaEventLogRepository extends JpaRepository<EventLogEntity, Long> {
    
    @Query("SELECT e FROM EventLogEntity e " +
           "WHERE e.className = :#{#eventLog.className} " +
           "AND e.method = :#{#eventLog.method} " +
           "AND SIZE(e.futureCalls) = :#{#eventLog.futureCalls.size()} " +
           "AND NOT EXISTS (" +
           "  SELECT fc FROM FutureCallEntity fc " +
           "  WHERE fc.eventLog = e " +
           "  AND fc.callName NOT IN :#{#eventLog.futureCalls}" +
           ")")
    Optional<EventLogEntity> findSameLog(@Param("eventLog") EventLog eventLog);
}

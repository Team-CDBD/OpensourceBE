package com.cdbd.opensource.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTopicRepository extends JpaRepository<TopicEntity, Long> {
}

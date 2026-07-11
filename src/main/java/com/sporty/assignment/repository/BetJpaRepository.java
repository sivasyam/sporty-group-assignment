package com.sporty.assignment.repository;

import com.sporty.assignment.entity.BetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BetJpaRepository extends JpaRepository<BetEntity, Long> {
    List<BetEntity> findByEventId(Long eventId);
}

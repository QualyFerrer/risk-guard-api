package com.cesar.riskguard.repository;

import com.cesar.riskguard.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Long> {

    List<Transaction> findByUserId (Long userId);

    int countByUserIdAndCreatedAtAfter (Long userId, LocalDateTime since);

    List<Transaction> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);


}

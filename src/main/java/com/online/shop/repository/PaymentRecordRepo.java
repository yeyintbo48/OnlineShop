package com.online.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.online.shop.entity.PaymentRecord;
import java.util.Optional;

@Repository
public interface PaymentRecordRepo extends JpaRepository<PaymentRecord,Long>{
    Optional<PaymentRecord> findByTransactionId(String transactionId);
}

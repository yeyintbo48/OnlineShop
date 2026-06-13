package com.online.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.online.shop.entity.PaymentRecord;
import java.util.Optional;

public interface PaymentRecordRepo extends JpaRepository<PaymentRecord,Long>{
    Optional<PaymentRecord> findByTransactionId(String transactionId);
}

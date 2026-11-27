package com.banking.banking_transaction_system.service;

import com.banking.banking_transaction_system.entity.IdempotencyRecord;

import java.util.Optional;

public interface IdempotencyService {

    Optional<IdempotencyRecord> findByKey(String idempotencyKey);

    IdempotencyRecord save(IdempotencyRecord record);
}
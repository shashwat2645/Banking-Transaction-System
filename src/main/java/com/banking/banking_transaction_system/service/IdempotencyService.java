package com.banking.banking_transaction_system.service;

import com.banking.banking_transaction_system.entity.IdempotencyRecord;

public interface IdempotencyService {

    IdempotencyRecord getByKey(String idempotencyKey);

    boolean exists(String idempotencyKey);

    IdempotencyRecord save(IdempotencyRecord record);
}
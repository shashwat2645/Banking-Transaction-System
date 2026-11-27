package com.banking.banking_transaction_system.service.impl;

import com.banking.banking_transaction_system.entity.IdempotencyRecord;
import com.banking.banking_transaction_system.exception.IdempotencyRecordNotFoundException;
import com.banking.banking_transaction_system.repository.IdempotencyRecordRepository;
import com.banking.banking_transaction_system.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyRecordRepository repository;

    @Override
    public IdempotencyRecord getByKey(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() ->
                        new IdempotencyRecordNotFoundException(
                                "Idempotency key not found."
                        ));
    }

    @Override
    public boolean exists(String idempotencyKey) {
        return repository.existsByIdempotencyKey(idempotencyKey);
    }

    @Override
    public IdempotencyRecord save(IdempotencyRecord record) {
        return repository.save(record);
    }
}
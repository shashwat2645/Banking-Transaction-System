package com.banking.banking_transaction_system.service.impl;

import com.banking.banking_transaction_system.entity.IdempotencyRecord;
import com.banking.banking_transaction_system.repository.IdempotencyRecordRepository;
import com.banking.banking_transaction_system.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyRecordRepository repository;

    @Override
    public Optional<IdempotencyRecord> findByKey(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey);
    }

    @Override
    public IdempotencyRecord save(IdempotencyRecord record) {
        return repository.save(record);
    }
}
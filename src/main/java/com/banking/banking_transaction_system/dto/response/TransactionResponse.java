package com.banking.banking_transaction_system.dto.response;

import com.banking.banking_transaction_system.enums.TransactionStatus;
import com.banking.banking_transaction_system.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private String transactionId;

    private String fromAccountNumber;

    private String toAccountNumber;

    private BigDecimal amount;

    private TransactionType transactionType;

    private TransactionStatus transactionStatus;

    private String description;

    private LocalDateTime createdAt;

}

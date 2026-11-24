package com.banking.banking_transaction_system.dto.response;

import com.banking.banking_transaction_system.enums.AccountStatus;
import com.banking.banking_transaction_system.enums.AccountType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private String accountNumber;

    private AccountType accountType;

    private BigDecimal balance;

    private AccountStatus status;
}
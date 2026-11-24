package com.banking.banking_transaction_system.dto.request;

import com.banking.banking_transaction_system.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Initial deposit cannot be negative")
    private BigDecimal initialDeposit;

    @NotNull(message = "Account type is required")
    private AccountType accountType;
}
package com.banking.banking_transaction_system.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Long id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String address;
}
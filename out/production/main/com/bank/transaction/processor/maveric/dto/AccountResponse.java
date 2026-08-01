package com.bank.transaction.processor.maveric.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResponse {

    private String accountId;

    private BigDecimal balance;

}
package com.bank.transaction.processor.maveric.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    /**
     * Unique account identifier.
     */
    private String accountId;

    /**
     * Current account balance.
     */
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Ledger of all transactions associated with this account.
     */
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
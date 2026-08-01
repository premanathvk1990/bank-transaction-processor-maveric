package com.bank.transaction.processor.maveric.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    /**
     * Unique transaction identifier.
     */
    @Builder.Default
    private String transactionId = UUID.randomUUID().toString();

    /**
     * Type of transaction.
     */
    private TransactionType transactionType;

    /**
     * Transaction amount.
     */
    private BigDecimal amount;

    /**
     * Account initiating the transaction.
     */
    private String fromAccountId;

    /**
     * Destination account (only for transfers).
     */
    private String toAccountId;

    /**
     * Balance after this transaction.
     */
    private BigDecimal balanceAfterTransaction;

    /**
     * Timestamp of transaction.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Optional remarks.
     */
    private String description;
}
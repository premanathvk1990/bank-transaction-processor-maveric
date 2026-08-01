package com.bank.transaction.processor.maveric.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String accountId) {
        super("Invalid amount : " + accountId);
    }
}

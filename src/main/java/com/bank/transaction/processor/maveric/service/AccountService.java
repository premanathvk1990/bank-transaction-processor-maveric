package com.bank.transaction.processor.maveric.service;

import com.bank.transaction.processor.maveric.model.Account;
import com.bank.transaction.processor.maveric.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account createAccount(String accountId, BigDecimal initialBalance);

    Account deposit(String accountId, BigDecimal amount);

    Account withdraw(String accountId, BigDecimal amount);

    void transfer(String fromAccountId,
                  String toAccountId,
                  BigDecimal amount);

    BigDecimal getBalance(String accountId);

    List<Transaction> getTransactionHistory(String accountId);

    Account getAccount(String accountId);
}

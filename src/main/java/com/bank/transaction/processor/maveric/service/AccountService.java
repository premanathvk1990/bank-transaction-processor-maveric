package com.bank.transaction.processor.maveric.service;
import com.bank.transaction.processor.maveric.exception.AccountAlreadyExistsException;
import com.bank.transaction.processor.maveric.exception.AccountNotFoundException;
import com.bank.transaction.processor.maveric.exception.InvalidAmountException;
import com.bank.transaction.processor.maveric.model.Account;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountService {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public Account createAccount(String accountId, BigDecimal initialBalance) {
        if (accounts.containsKey(accountId)) {
            throw new AccountAlreadyExistsException(
                    "Account already exists");
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }

        Account account = Account.builder()
                .accountId(accountId)
                .balance(initialBalance)
                .build();

        accounts.put(accountId, account);

        return account;
    }

    public Account deposit(String accountId, BigDecimal amount) {

        Account account = accounts.get(accountId);

        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }

        account.setBalance(account.getBalance().add(amount));

        return account;
    }
}

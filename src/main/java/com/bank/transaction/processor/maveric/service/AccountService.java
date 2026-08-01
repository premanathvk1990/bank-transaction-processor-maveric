package com.bank.transaction.processor.maveric.service;
import com.bank.transaction.processor.maveric.exception.AccountAlreadyExistsException;
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

        Account account = Account.builder()
                .accountId(accountId)
                .balance(initialBalance)
                .build();

        accounts.put(accountId, account);

        return account;
    }
}

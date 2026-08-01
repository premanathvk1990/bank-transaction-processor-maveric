package com.bank.transaction.processor.maveric.repository;

import com.bank.transaction.processor.maveric.model.Account;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public  class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {

        accounts.put(account.getAccountId(), account);

        return account;
    }

    @Override
    public Optional<Account> findById(String accountId) {

        return Optional.ofNullable(accounts.get(accountId));
    }

    @Override
    public boolean exists(String accountId) {

        return accounts.containsKey(accountId);
    }
}

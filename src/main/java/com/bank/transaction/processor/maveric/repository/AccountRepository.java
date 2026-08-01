package com.bank.transaction.processor.maveric.repository;

import com.bank.transaction.processor.maveric.model.Account;

import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(String accountId);

    boolean exists(String accountId);

    void clear();
}

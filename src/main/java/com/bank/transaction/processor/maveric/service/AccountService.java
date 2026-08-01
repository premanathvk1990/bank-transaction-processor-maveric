package com.bank.transaction.processor.maveric.service;
import com.bank.transaction.processor.maveric.exception.AccountAlreadyExistsException;
import com.bank.transaction.processor.maveric.exception.AccountNotFoundException;
import com.bank.transaction.processor.maveric.exception.InsufficientFundsException;
import com.bank.transaction.processor.maveric.exception.InvalidAmountException;
import com.bank.transaction.processor.maveric.model.Account;
import com.bank.transaction.processor.maveric.model.Transaction;
import com.bank.transaction.processor.maveric.model.TransactionType;

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

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero");
        }
        Account account = accounts.get(accountId);

        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }

        account.setBalance(account.getBalance().add(amount));
        recordTransaction(account, TransactionType.DEPOSIT, amount, accountId, null, "Cash Deposit");
        return account;
    }

    public Account withdraw(String accountId, BigDecimal amount) {

        Account account = accounts.get(accountId);

        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAmountException("Withdrawal amount must be greater than zero");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountId);
        }
        account.setBalance(account.getBalance().subtract(amount));
        recordTransaction(account, TransactionType.WITHDRAW, amount, accountId, null, "Cash Withdrawal");
        return account;
    }

    private void recordTransaction(
            Account account,
            TransactionType type,
            BigDecimal amount,
            String fromAccountId,
            String toAccountId,
            String description) {

        Transaction transaction = Transaction.builder()
                .transactionType(type)
                .amount(amount)
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .balanceAfterTransaction(account.getBalance())
                .description(description)
                .build();

        account.getTransactions().add(transaction);
    }
}

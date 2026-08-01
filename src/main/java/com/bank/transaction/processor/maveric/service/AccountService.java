package com.bank.transaction.processor.maveric.service;
import com.bank.transaction.processor.maveric.exception.*;
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
        Account account = getExistingAccount(accountId);

        account.setBalance(account.getBalance().add(amount));
        recordTransaction(account, TransactionType.DEPOSIT, amount, accountId, null, "Cash Deposit");
        return account;
    }

    public Account withdraw(String accountId, BigDecimal amount) {

        Account account = getExistingAccount(accountId);

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

    public void transfer(String fromAccountId,
                         String toAccountId,
                         BigDecimal amount) {
        Account source = getExistingAccount(fromAccountId);

        Account destination = getExistingAccount(toAccountId);

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAmountException(
                    "Transfer amount must be greater than zero");
        }

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromAccountId);
        }
        if (fromAccountId.equals(toAccountId)) {
            throw new InvalidTransferException();
        }
        source.setBalance(source.getBalance().subtract(amount));

        destination.setBalance(destination.getBalance().add(amount));
        recordTransaction(
                source,
                TransactionType.TRANSFER,
                amount,
                fromAccountId,
                toAccountId,
                "Transfer Sent");

        recordTransaction(
                destination,
                TransactionType.TRANSFER,
                amount,
                fromAccountId,
                toAccountId,
                "Transfer Received");
    }

    private Account getExistingAccount(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
        return account;
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }
}

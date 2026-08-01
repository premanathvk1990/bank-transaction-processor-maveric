package com.bank.transaction.processor.maveric.service;

import com.bank.transaction.processor.maveric.exception.*;
import com.bank.transaction.processor.maveric.model.Account;
import com.bank.transaction.processor.maveric.model.Transaction;
import com.bank.transaction.processor.maveric.model.TransactionType;
import com.bank.transaction.processor.maveric.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    public Account createAccount(String accountId, BigDecimal initialBalance) {
        if (repository.exists(accountId)) {
            throw new AccountAlreadyExistsException(accountId);
        }

        validateAmount(initialBalance, "Initial balance");

        Account account = Account.builder()
                .accountId(accountId)
                .balance(initialBalance)
                .build();

        return repository.save(account);
    }

    @Override
    public Account deposit(String accountId, BigDecimal amount) {
        validateAmount(amount, "Deposit amount");

        Account account = getExistingAccount(accountId);

        account.setBalance(account.getBalance().add(amount));

        recordTransaction(
                account,
                TransactionType.DEPOSIT,
                amount,
                accountId,
                null,
                "Cash Deposit");

        return repository.save(account);
    }

    @Override
    public Account withdraw(String accountId, BigDecimal amount) {
        validateAmount(amount, "Withdrawal amount");

        Account account = getExistingAccount(accountId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountId);
        }

        account.setBalance(account.getBalance().subtract(amount));

        recordTransaction(
                account,
                TransactionType.WITHDRAW,
                amount,
                accountId,
                null,
                "Cash Withdrawal");

        return repository.save(account);
    }

    @Override
    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        validateAmount(amount, "Transfer amount");

        if (fromAccountId.equals(toAccountId)) {
            throw new InvalidTransferException();
        }

        Account source = getExistingAccount(fromAccountId);
        Account destination = getExistingAccount(toAccountId);

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromAccountId);
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

        repository.save(source);
        repository.save(destination);
    }

    @Override
    public BigDecimal getBalance(String accountId) {
        return getExistingAccount(accountId).getBalance();
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountId) {
        return List.copyOf(
                getExistingAccount(accountId).getTransactions());
    }

    @Override
    public Account getAccount(String accountId) {
        return getExistingAccount(accountId);
    }

    private Account getExistingAccount(String accountId) {

        return repository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountId));
    }

    private void validateAmount(BigDecimal amount,
                                String field) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAmountException(
                    field + " must be greater than zero");
        }
    }

    private void recordTransaction(Account account,
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

package com.bank.transaction.processor.maveric.service;

import com.bank.transaction.processor.maveric.exception.AccountAlreadyExistsException;
import com.bank.transaction.processor.maveric.exception.AccountNotFoundException;
import com.bank.transaction.processor.maveric.exception.InsufficientFundsException;
import com.bank.transaction.processor.maveric.exception.InvalidAmountException;
import com.bank.transaction.processor.maveric.exception.InvalidTransferException;
import com.bank.transaction.processor.maveric.model.Account;
import com.bank.transaction.processor.maveric.model.TransactionType;
import com.bank.transaction.processor.maveric.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account createAccount(String id, String balance) {
        return Account.builder()
                .accountId(id)
                .balance(new BigDecimal(balance))
                .transactions(new ArrayList<>())
                .build();
    }

    @Test
    void shouldCreateAccountSuccessfully() {

        when(repository.exists("ACC1001")).thenReturn(false);

        when(repository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account account = accountService.createAccount(
                "ACC1001",
                new BigDecimal("1000"));

        assertEquals("ACC1001", account.getAccountId());
        assertEquals(0,
                account.getBalance().compareTo(new BigDecimal("1000")));

        verify(repository).save(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenAccountAlreadyExists() {

        when(repository.exists("ACC1001")).thenReturn(true);

        assertThrows(AccountAlreadyExistsException.class,
                () -> accountService.createAccount(
                        "ACC1001",
                        new BigDecimal("1000")));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldRejectInvalidInitialBalance() {

        assertThrows(InvalidAmountException.class,
                () -> accountService.createAccount(
                        "ACC1001",
                        BigDecimal.ZERO));
    }

    @Test
    void shouldDepositSuccessfully() {

        Account account = createAccount("ACC1001", "1000");

        when(repository.findById("ACC1001"))
                .thenReturn(Optional.of(account));

        when(repository.save(any(Account.class)))
                .thenAnswer(i -> i.getArgument(0));

        Account result = accountService.deposit(
                "ACC1001",
                new BigDecimal("500"));

        assertEquals(0,
                result.getBalance().compareTo(new BigDecimal("1500")));

        assertEquals(1, result.getTransactions().size());

        assertEquals(TransactionType.DEPOSIT,
                result.getTransactions().get(0).getTransactionType());
    }

    @Test
    void shouldThrowAccountNotFoundForDeposit() {

        when(repository.findById("ACC1001"))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.deposit(
                        "ACC1001",
                        new BigDecimal("100")));
    }

    @Test
    void shouldWithdrawSuccessfully() {

        Account account = createAccount("ACC1001", "1000");

        when(repository.findById("ACC1001"))
                .thenReturn(Optional.of(account));

        when(repository.save(any(Account.class)))
                .thenAnswer(i -> i.getArgument(0));

        Account result = accountService.withdraw(
                "ACC1001",
                new BigDecimal("200"));

        assertEquals(0,
                result.getBalance().compareTo(new BigDecimal("800")));

        assertEquals(1,
                result.getTransactions().size());

        assertEquals(TransactionType.WITHDRAW,
                result.getTransactions().get(0).getTransactionType());
    }

    @Test
    void shouldThrowInsufficientFundsException() {

        Account account = createAccount("ACC1001", "100");

        when(repository.findById("ACC1001"))
                .thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class,
                () -> accountService.withdraw(
                        "ACC1001",
                        new BigDecimal("200")));
    }

    @Test
    void shouldTransferSuccessfully() {

        Account source = createAccount("ACC1001", "1000");
        Account destination = createAccount("ACC2001", "500");

        when(repository.findById("ACC1001"))
                .thenReturn(Optional.of(source));

        when(repository.findById("ACC2001"))
                .thenReturn(Optional.of(destination));

        when(repository.save(any(Account.class)))
                .thenAnswer(i -> i.getArgument(0));

        accountService.transfer(
                "ACC1001",
                "ACC2001",
                new BigDecimal("300"));

        assertEquals(0,
                source.getBalance().compareTo(new BigDecimal("700")));

        assertEquals(0,
                destination.getBalance().compareTo(new BigDecimal("800")));

        verify(repository, times(2))
                .save(any(Account.class));
    }

    @Test
    void shouldRejectTransferToSameAccount() {

        assertThrows(InvalidTransferException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC1001",
                        new BigDecimal("100")));
    }

    @Test
    void shouldReturnBalance() {

        Account account = createAccount("ACC1001", "2500");

        when(repository.findById("ACC1001"))
                .thenReturn(Optional.of(account));

        BigDecimal balance = accountService.getBalance("ACC1001");

        assertEquals(0,
                balance.compareTo(new BigDecimal("2500")));
    }

    @Test
    void shouldReturnTransactionHistory() {

        Account account = createAccount("ACC1001", "1000");

        accountService = new AccountServiceImpl(repository);

        account.getTransactions().add(
                com.bank.transaction.processor.maveric.model.Transaction
                        .builder()
                        .transactionType(TransactionType.DEPOSIT)
                        .amount(new BigDecimal("100"))
                        .build());

        when(repository.findById("ACC1001"))
                .thenReturn(Optional.of(account));

        assertEquals(1,
                accountService.getTransactionHistory("ACC1001").size());
    }
}
package com.bank.transaction.processor.maveric.service;

import com.bank.transaction.processor.maveric.exception.AccountAlreadyExistsException;
import com.bank.transaction.processor.maveric.exception.AccountNotFoundException;
import com.bank.transaction.processor.maveric.exception.InvalidAmountException;
import com.bank.transaction.processor.maveric.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void shouldCreateAccountSuccessfully() {

        Account account = accountService.createAccount(
                "ACC1001",
                new BigDecimal("1000.00")
        );

        assertNotNull(account);
        assertEquals("ACC1001", account.getAccountId());
        assertEquals(
                0,
                new BigDecimal("1000.00").compareTo(account.getBalance())
        );
    }

    @Test
    void shouldThrowExceptionWhenAccountAlreadyExists() {

        accountService.createAccount("ACC1001", new BigDecimal("1000"));

        assertThrows(AccountAlreadyExistsException.class,
                () -> accountService.createAccount("ACC1001",
                        new BigDecimal("500")));
    }

    @Test
    void shouldRejectNegativeInitialBalance() {

        assertThrows(InvalidAmountException.class,
                () -> accountService.createAccount(
                        "ACC1001",
                        new BigDecimal("-100")));
    }

    @Test
    void shouldDepositAmountSuccessfully() {

        // Arrange
        accountService.createAccount("ACC1001", new BigDecimal("1000.00"));

        // Act
        Account account = accountService.deposit(
                "ACC1001",
                new BigDecimal("500.00"));

        // Assert
        assertEquals(
                0,
                new BigDecimal("1500.00").compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.deposit(
                        "ACC9999",
                        new BigDecimal("100")));
    }

    @Test
    void shouldRejectNegativeDepositAmount() {

        accountService.createAccount("ACC1001",
                new BigDecimal("1000"));

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.deposit(
                        "ACC1001",
                        new BigDecimal("-100")));
    }
}

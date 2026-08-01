package com.bank.transaction.processor.maveric.service;

import com.bank.transaction.processor.maveric.exception.*;
import com.bank.transaction.processor.maveric.model.Account;
import com.bank.transaction.processor.maveric.model.Transaction;
import com.bank.transaction.processor.maveric.model.TransactionType;
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

    @Test
    void shouldCreateTransactionAfterDeposit() {

        accountService.createAccount(
                "ACC1001",
                new BigDecimal("1000"));

        Account account = accountService.deposit(
                "ACC1001",
                new BigDecimal("200"));

        assertEquals(1, account.getTransactions().size());

        Transaction transaction =
                account.getTransactions().getFirst();

        assertEquals(TransactionType.DEPOSIT,
                transaction.getTransactionType());

        assertEquals(
                0,
                new BigDecimal("200")
                        .compareTo(transaction.getAmount()));
    }

    @Test
    void shouldWithdrawAmountSuccessfully() {

        // Arrange
        accountService.createAccount(
                "ACC1001",
                new BigDecimal("1000.00"));

        // Act
        Account account = accountService.withdraw(
                "ACC1001",
                new BigDecimal("300.00"));

        // Assert
        assertEquals(
                0,
                new BigDecimal("700.00")
                        .compareTo(account.getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenWithdrawFromUnknownAccount() {

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.withdraw(
                        "ACC9999",
                        new BigDecimal("100")));
    }

    @Test
    void shouldRejectNegativeWithdrawalAmount() {

        accountService.createAccount(
                "ACC1001",
                new BigDecimal("1000"));

        assertThrows(
                InvalidAmountException.class,
                () -> accountService.withdraw(
                        "ACC1001",
                        new BigDecimal("-100")));
    }

    @Test
    void shouldRejectWithdrawalWhenInsufficientFunds() {

        accountService.createAccount(
                "ACC1001",
                new BigDecimal("100"));

        assertThrows(
                InsufficientFundsException.class,
                () -> accountService.withdraw(
                        "ACC1001",
                        new BigDecimal("200")));
    }

    @Test
    void shouldRecordWithdrawalTransaction() {

        accountService.createAccount(
                "ACC1001",
                new BigDecimal("1000"));

        Account account = accountService.withdraw(
                "ACC1001",
                new BigDecimal("250"));

        assertEquals(1, account.getTransactions().size());

        Transaction transaction = account.getTransactions().get(0);

        assertEquals(
                TransactionType.WITHDRAW,
                transaction.getTransactionType());

        assertEquals(
                0,
                new BigDecimal("250")
                        .compareTo(transaction.getAmount()));
    }

    @Test
    void shouldTransferMoneySuccessfully() {

        // Arrange
        accountService.createAccount("ACC1001", new BigDecimal("1000"));
        accountService.createAccount("ACC2001", new BigDecimal("500"));

        // Act
        accountService.transfer(
                "ACC1001",
                "ACC2001",
                new BigDecimal("300"));

        // Assert
        assertEquals(0,
                new BigDecimal("700")
                        .compareTo(accountService.getAccount("ACC1001").getBalance()));

        assertEquals(0,
                new BigDecimal("800")
                        .compareTo(accountService.getAccount("ACC2001").getBalance()));
    }

    @Test
    void shouldThrowExceptionWhenSourceAccountNotFound() {

        accountService.createAccount("ACC2001",
                new BigDecimal("500"));

        assertThrows(AccountNotFoundException.class,
                () -> accountService.transfer(
                        "ACC9999",
                        "ACC2001",
                        new BigDecimal("100")));
    }

    @Test
    void shouldThrowExceptionWhenDestinationAccountNotFound() {

        accountService.createAccount("ACC1001",
                new BigDecimal("1000"));

        assertThrows(AccountNotFoundException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC9999",
                        new BigDecimal("100")));
    }

    @Test
    void shouldRejectNegativeTransferAmount() {

        accountService.createAccount("ACC1001",
                new BigDecimal("1000"));

        accountService.createAccount("ACC2001",
                new BigDecimal("500"));

        assertThrows(InvalidAmountException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC2001",
                        new BigDecimal("-100")));
    }

    @Test
    void shouldRejectTransferWhenInsufficientFunds() {

        accountService.createAccount("ACC1001",
                new BigDecimal("100"));

        accountService.createAccount("ACC2001",
                new BigDecimal("500"));

        assertThrows(InsufficientFundsException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC2001",
                        new BigDecimal("200")));
    }

    @Test
    void shouldRejectTransferToSameAccount() {

        accountService.createAccount("ACC1001",
                new BigDecimal("1000"));

        assertThrows(InvalidTransferException.class,
                () -> accountService.transfer(
                        "ACC1001",
                        "ACC1001",
                        new BigDecimal("100")));
    }

    @Test
    void shouldRecordTransferTransactions() {

        accountService.createAccount("ACC1001",
                new BigDecimal("1000"));

        accountService.createAccount("ACC2001",
                new BigDecimal("500"));

        accountService.transfer(
                "ACC1001",
                "ACC2001",
                new BigDecimal("300"));

        assertEquals(1,
                accountService.getAccount("ACC1001")
                        .getTransactions().size());

        assertEquals(1,
                accountService.getAccount("ACC2001")
                        .getTransactions().size());
    }

    @Test
    void shouldReturnAccountBalance() {

        // Arrange
        accountService.createAccount(
                "ACC1001",
                new BigDecimal("1000"));

        accountService.deposit(
                "ACC1001",
                new BigDecimal("500"));

        // Act
        BigDecimal balance = accountService.getBalance("ACC1001");

        // Assert
        assertEquals(
                0,
                new BigDecimal("1500")
                        .compareTo(balance));
    }

    @Test
    void shouldThrowExceptionWhenBalanceRequestedForUnknownAccount() {

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getBalance("ACC9999"));
    }
}

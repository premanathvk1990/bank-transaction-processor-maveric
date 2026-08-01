package com.bank.transaction.processor.maveric.controller;

import com.bank.transaction.processor.maveric.dto.*;
import com.bank.transaction.processor.maveric.model.Account;
import com.bank.transaction.processor.maveric.model.Transaction;
import com.bank.transaction.processor.maveric.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(
            @Valid @RequestBody CreateAccountRequest request) {

        Account account = accountService.createAccount(
                request.getAccountId(),
                request.getInitialBalance());

        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .balance(account.getBalance())
                .build();
    }

    @PostMapping("/{accountId}/deposit")
    public AccountResponse deposit(
            @PathVariable String accountId,
            @Valid @RequestBody DepositRequest request) {

        Account account = accountService.deposit(
                accountId,
                request.getAmount());

        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .balance(account.getBalance())
                .build();
    }

    @PostMapping("/{accountId}/withdraw")
    public AccountResponse withdraw(
            @PathVariable String accountId,
            @Valid @RequestBody WithdrawRequest request) {

        Account account = accountService.withdraw(
                accountId,
                request.getAmount());

        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .balance(account.getBalance())
                .build();
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public void transfer(
            @Valid @RequestBody TransferRequest request) {

        accountService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount());
    }

    @GetMapping("/{accountId}/balance")
    public BigDecimal getBalance(
            @PathVariable String accountId) {

        return accountService.getBalance(accountId);
    }

    @GetMapping("/{accountId}/transactions")
    public List<Transaction> getTransactionHistory(
            @PathVariable String accountId) {

        return accountService.getTransactionHistory(accountId);
    }
}
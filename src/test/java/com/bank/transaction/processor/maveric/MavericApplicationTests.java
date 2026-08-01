package com.bank.transaction.processor.maveric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bank.transaction.processor.maveric.exception.*;
import com.bank.transaction.processor.maveric.model.Account;
import com.bank.transaction.processor.maveric.model.Transaction;
import com.bank.transaction.processor.maveric.model.TransactionType;
import org.springframework.test.annotation.DirtiesContext;

import com.bank.transaction.processor.maveric.repository.AccountRepository;
import com.bank.transaction.processor.maveric.service.AccountService;
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MavericApplicationTests {

		@Autowired
		private AccountRepository repository;

		@Autowired
		private AccountService accountService;

		@BeforeEach
		void setUp() {
		// accountService = new AccountService();
		repository.clear();
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

		accountService.createAccount("ACC1002", new BigDecimal("1000"));

		assertThrows(AccountAlreadyExistsException.class,
				() -> accountService.createAccount("ACC1002",
						new BigDecimal("500")));
	}

		@Test
		void shouldRejectNegativeInitialBalance() {

		assertThrows(InvalidAmountException.class,
				() -> accountService.createAccount(
						"ACC1003",
						new BigDecimal("-100")));
	}

		@Test
		void shouldDepositAmountSuccessfully() {

		// Arrange
		accountService.createAccount("ACC1004", new BigDecimal("1000.00"));

		// Act
		Account account = accountService.deposit(
				"ACC1004",
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

		accountService.createAccount("ACC1005",
				new BigDecimal("1000"));

		assertThrows(
				InvalidAmountException.class,
				() -> accountService.deposit(
						"ACC1005",
						new BigDecimal("-100")));
	}

		@Test
		void shouldCreateTransactionAfterDeposit() {

		accountService.createAccount(
				"ACC1006",
				new BigDecimal("1000"));

		Account account = accountService.deposit(
				"ACC1006",
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
				"ACC1007",
				new BigDecimal("1000.00"));

		// Act
		Account account = accountService.withdraw(
				"ACC1007",
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
				"ACC1008",
				new BigDecimal("1000"));

		assertThrows(
				InvalidAmountException.class,
				() -> accountService.withdraw(
						"ACC1008",
						new BigDecimal("-100")));
	}

		@Test
		void shouldRejectWithdrawalWhenInsufficientFunds() {

		accountService.createAccount(
				"ACC1009",
				new BigDecimal("100"));

		assertThrows(
				InsufficientFundsException.class,
				() -> accountService.withdraw(
						"ACC1009",
						new BigDecimal("200")));
	}

		@Test
		void shouldRecordWithdrawalTransaction() {

		accountService.createAccount(
				"ACC1010",
				new BigDecimal("1000"));

		Account account = accountService.withdraw(
				"ACC1010",
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
		accountService.createAccount("ACC1011", new BigDecimal("1000"));
		accountService.createAccount("ACC2012", new BigDecimal("500"));

		// Act
		accountService.transfer(
				"ACC1011",
				"ACC2012",
				new BigDecimal("300"));

		// Assert
		assertEquals(0,
				new BigDecimal("700")
						.compareTo(accountService.getAccount("ACC1011").getBalance()));

		assertEquals(0,
				new BigDecimal("800")
						.compareTo(accountService.getAccount("ACC2012").getBalance()));
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

		accountService.createAccount("ACC1013",
				new BigDecimal("1000"));

		assertThrows(AccountNotFoundException.class,
				() -> accountService.transfer(
						"ACC1013",
						"ACC9999",
						new BigDecimal("100")));
	}

		@Test
		void shouldRejectNegativeTransferAmount() {

		accountService.createAccount("ACC1014",
				new BigDecimal("1000"));

		accountService.createAccount("ACC2015",
				new BigDecimal("500"));

		assertThrows(InvalidAmountException.class,
				() -> accountService.transfer(
						"ACC1014",
						"ACC2015",
						new BigDecimal("-100")));
	}

		@Test
		void shouldRejectTransferWhenInsufficientFunds() {

		accountService.createAccount("ACC1016",
				new BigDecimal("100"));

		accountService.createAccount("ACC2017",
				new BigDecimal("500"));

		assertThrows(InsufficientFundsException.class,
				() -> accountService.transfer(
						"ACC1016",
						"ACC2017",
						new BigDecimal("200")));
	}

		@Test
		void shouldRejectTransferToSameAccount() {

		accountService.createAccount("ACC1018",
				new BigDecimal("1000"));

		assertThrows(InvalidTransferException.class,
				() -> accountService.transfer(
						"ACC1018",
						"ACC1018",
						new BigDecimal("100")));
	}

		@Test
		void shouldRecordTransferTransactions() {

		accountService.createAccount("ACC1019",
				new BigDecimal("1000"));

		accountService.createAccount("ACC2020",
				new BigDecimal("500"));

		accountService.transfer(
				"ACC1019",
				"ACC2020",
				new BigDecimal("300"));

		assertEquals(1,
				accountService.getAccount("ACC1019")
						.getTransactions().size());

		assertEquals(1,
				accountService.getAccount("ACC2020")
						.getTransactions().size());
	}

		@Test
		void shouldReturnAccountBalance() {

		// Arrange
		accountService.createAccount(
				"ACC1021",
				new BigDecimal("1000"));

		accountService.deposit(
				"ACC1021",
				new BigDecimal("500"));

		// Act
		BigDecimal balance = accountService.getBalance("ACC1021");

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

		@Test
		void shouldReturnTransactionHistory() {

		accountService.createAccount(
				"ACC1022",
				new BigDecimal("1000"));

		accountService.deposit(
				"ACC1022",
				new BigDecimal("200"));

		accountService.withdraw(
				"ACC1022",
				new BigDecimal("100"));

		List<Transaction> history =
				accountService.getTransactionHistory("ACC1022");

		assertEquals(2, history.size());
	}

		@Test
		void shouldReturnTransactionsInChronologicalOrder() {

		accountService.createAccount(
				"ACC1023",
				new BigDecimal("1000"));

		accountService.deposit(
				"ACC1023",
				new BigDecimal("200"));

		accountService.withdraw(
				"ACC1023",
				new BigDecimal("50"));

		List<Transaction> history =
				accountService.getTransactionHistory("ACC1023");

		assertEquals(
				TransactionType.DEPOSIT,
				history.get(0).getTransactionType());

		assertEquals(
				TransactionType.WITHDRAW,
				history.get(1).getTransactionType());
	}

		@Test
		void shouldReturnImmutableTransactionHistory() {

		accountService.createAccount(
				"ACC1024",
				new BigDecimal("1000"));

		List<Transaction> history =
				accountService.getTransactionHistory("ACC1024");

		assertThrows(
				UnsupportedOperationException.class,
				() -> history.add(Transaction.builder().build()));
	}

}

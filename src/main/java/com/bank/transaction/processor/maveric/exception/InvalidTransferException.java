package com.bank.transaction.processor.maveric.exception;

public class InvalidTransferException extends RuntimeException {

  public InvalidTransferException() {
    super("Source and destination accounts cannot be the same");
  }
}

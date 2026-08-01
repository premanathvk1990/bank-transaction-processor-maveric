package com.bank.transaction.processor.maveric.exception;

import com.bank.transaction.processor.maveric.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AccountNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleAccountNotFound(
          AccountNotFoundException ex,
          HttpServletRequest request) {

    return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            request.getRequestURI());
  }

  @ExceptionHandler(AccountAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateAccount(
          AccountAlreadyExistsException ex,
          HttpServletRequest request) {

    return buildErrorResponse(
            HttpStatus.CONFLICT,
            ex.getMessage(),
            request.getRequestURI());
  }

  @ExceptionHandler({
          InvalidAmountException.class,
          InvalidTransferException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequest(
          RuntimeException ex,
          HttpServletRequest request) {

    return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            request.getRequestURI());
  }

  @ExceptionHandler(InsufficientFundsException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientFunds(
          InsufficientFundsException ex,
          HttpServletRequest request) {

    return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            request.getRequestURI());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
          MethodArgumentNotValidException ex,
          HttpServletRequest request) {

    String message = ex.getBindingResult()
            .getFieldError()
            .getDefaultMessage();

    return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            message,
            request.getRequestURI());
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
          HttpStatus status,
          String message,
          String path) {

    ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(path)
            .build();

    return ResponseEntity.status(status).body(response);
  }
}
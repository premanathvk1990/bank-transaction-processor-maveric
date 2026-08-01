package com.bank.transaction.processor.maveric.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {

    @NotBlank
    private String accountId;

    @NotNull
    private BigDecimal initialBalance;

}

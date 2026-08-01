package com.bank.transaction.processor.maveric.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawRequest {

    @NotNull
    private BigDecimal amount;

}

package com.demo.transaction.dto.request;

import com.demo.transaction.enumeration.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotNull
    private TransactionType type;

    @NotNull
    private Long fromAccountId;  // For deposit, withdraw, and transfer

    private Long toAccountId;    // For transfer

    @NotNull
    @DecimalMin("0.0000")
    private BigDecimal amount;

    @Size(max = 255)
    private String description;
} 
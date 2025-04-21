package com.demo.transaction.dto.request;

import com.demo.transaction.enumeration.TransactionType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private TransactionType type;
    private Long fromAccountId;  // For deposit, withdraw, and transfer
    private Long toAccountId;    // For transfer
    private BigDecimal amount;
    private String description;
} 
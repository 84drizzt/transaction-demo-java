package com.demo.transaction.dto;

import com.demo.transaction.enumeration.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long id;
    private String transactionNumber;
    private Long accountId;
    private TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceNumber;
    private Long relatedAccountId;
    private LocalDateTime transactionTime;
} 
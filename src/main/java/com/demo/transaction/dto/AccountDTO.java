package com.demo.transaction.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountDTO {
    private Long id;
    private String accountNumber;
    private Long userId;
    private BigDecimal balance;
    private String currency;
} 
package com.demo.transaction.dto.request;

import lombok.Data;

/**
 * 交易更新请求DTO
 */
@Data
public class TransactionUpdateRequest {

    private String description;
    private String referenceNumber;
} 
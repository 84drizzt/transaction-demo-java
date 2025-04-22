package com.demo.transaction.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 交易更新请求DTO
 */
@Data
public class TransactionUpdateRequest {

    @Size(max = 255)
    private String description;

    @Size(max = 50)
    private String referenceNumber;
} 
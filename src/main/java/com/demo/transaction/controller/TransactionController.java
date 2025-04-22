package com.demo.transaction.controller;

import com.demo.transaction.dto.TransactionDTO;
import com.demo.transaction.dto.request.TransactionRequest;
import com.demo.transaction.dto.request.TransactionUpdateRequest;
import com.demo.transaction.dto.response.ApiResponse;
import com.demo.transaction.service.TransactionService;
import com.demo.transaction.strategy.TransactionHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionHandler transactionHandler;


    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> listTransactionsWithParameters(
            TransactionDTO transactionDTO,
            @DecimalMax(value = "100", message = "page must be 0~100")
            @RequestParam(defaultValue = "0") int page,
            @DecimalMin(value = "1", message = "size must be 1~100")
            @DecimalMax(value = "100", message = "size must be 1~100")
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @Pattern(regexp = "asc|desc")
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        List<TransactionDTO> transactions = transactionService.getTransactionsByParameters(transactionDTO, pageable);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(transaction -> ResponseEntity.ok(ApiResponse.success(transaction)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("交易不存在")));
    }


    @PostMapping()
    public ResponseEntity<ApiResponse<TransactionDTO>> processTransaction(@Valid @RequestBody TransactionRequest request) {
        try {
            TransactionDTO dto = transactionHandler.handle(request);
            return ResponseEntity.ok(ApiResponse.success("交易成功", dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDTO>> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionUpdateRequest request) {
        try {
            TransactionDTO updatedTransaction = transactionService.updateTransaction(id, request);
            return ResponseEntity.ok(ApiResponse.success("Transaction Updated", updatedTransaction));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> softDeleteTransaction(@PathVariable Long id) {
        try {
            transactionService.softDeleteTransaction(id);
            return ResponseEntity.ok(ApiResponse.success("Transaction Deleted", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
} 
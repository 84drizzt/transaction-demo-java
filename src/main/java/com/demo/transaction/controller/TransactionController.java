package com.demo.transaction.controller;

import ch.qos.logback.classic.model.processor.ConfigurationModelHandler;
import com.demo.transaction.dto.TransactionDTO;
import com.demo.transaction.dto.UserDTO;
import com.demo.transaction.dto.request.TransactionRequest;
import com.demo.transaction.dto.response.ApiResponse;
import com.demo.transaction.service.TransactionService;
import com.demo.transaction.strategy.TransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
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
                        .body(ApiResponse.error("Transaction Not Found")));
    }


    @PostMapping()
    public ResponseEntity<ApiResponse<TransactionDTO>> processTransaction(@RequestBody TransactionRequest request) {
        try {
            TransactionDTO dto = transactionHandler.handle(request);
            return ResponseEntity.ok(ApiResponse.success("交易成功", dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 
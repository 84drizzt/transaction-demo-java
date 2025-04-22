package com.demo.transaction.controller;

import com.demo.transaction.dto.AccountDTO;
import com.demo.transaction.dto.response.ApiResponse;
import com.demo.transaction.service.AccountService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;

    /* TODO: when need to implement:
           - create Account
           - update Account
           - delete Account
    */

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AccountDTO>>> listAccountsWithParameters(
            AccountDTO accountDTO,
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
        Page<AccountDTO> accounts = accountService.getAccountsByParameters(accountDTO, pageable);
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(account -> ResponseEntity.ok(ApiResponse.success(account)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Account Not Found")));
    }


} 
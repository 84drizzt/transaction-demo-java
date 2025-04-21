package com.demo.transaction.controller;

import com.demo.transaction.dto.AccountDTO;
import com.demo.transaction.dto.response.ApiResponse;
import com.demo.transaction.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountService accountService;

    /* TODO:
           - createAccount
           - updateAccount
           - deleteAccount
    */

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountDTO>>> listAccountsWithParameters(
            AccountDTO accountDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        List<AccountDTO> accounts = accountService.getAccountsByParameters(accountDTO, pageable);
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
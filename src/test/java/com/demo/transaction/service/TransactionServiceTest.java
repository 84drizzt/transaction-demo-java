package com.demo.transaction.service;

import com.demo.transaction.dto.TransactionDTO;
import com.demo.transaction.dto.request.TransactionUpdateRequest;
import com.demo.transaction.entity.Account;
import com.demo.transaction.entity.Transaction;
import com.demo.transaction.enumeration.TransactionType;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;
    private Account account;
    private static final Long TRANSACTION_ID = 1L;
    private static final Long ACCOUNT_ID = 1L;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(ACCOUNT_ID);
        account.setBalance(new BigDecimal("1000.00"));

        transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setBalanceBefore(new BigDecimal("900.00"));
        transaction.setBalanceAfter(new BigDecimal("1000.00"));
        transaction.setDescription("Test transaction");
        transaction.setReferenceNumber("REF123");
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setDeleted(false);
    }

    @Test
    void getTransactionsByParameters_ShouldReturnTransactionPage() {
        // 准备测试数据
        TransactionDTO searchDTO = new TransactionDTO();
        searchDTO.setAccountId(ACCOUNT_ID);

        Page<Transaction> transactionPage = new PageImpl<>(Collections.singletonList(transaction));
        when(transactionRepository.findAll((Specification<Transaction>) any(), any(Pageable.class)))
                .thenReturn(transactionPage);

        // 执行测试
        Page<TransactionDTO> result = transactionService.getTransactionsByParameters(searchDTO, Pageable.unpaged());

        // 验证结果
        assertEquals(1, result.getContent().size());
        assertEquals(TRANSACTION_ID, result.getContent().get(0).getId());
        assertEquals(ACCOUNT_ID, result.getContent().get(0).getAccountId());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateTransaction_Success() {
        // 准备测试数据
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        request.setDescription("Updated description");
        request.setReferenceNumber("Updated REF");

        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        // 执行测试
        TransactionDTO result = transactionService.updateTransaction(TRANSACTION_ID, request);

        // 验证结果
        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        assertEquals("Updated REF", result.getReferenceNumber());
        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_NotFound() {
        // 准备测试数据
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> 
            transactionService.updateTransaction(TRANSACTION_ID, request)
        );
        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_AlreadyDeleted() {
        // 准备测试数据
        transaction.setDeleted(true);
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Optional.of(transaction));

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> 
            transactionService.updateTransaction(TRANSACTION_ID, request)
        );
        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void softDeleteTransaction_Success() {
        // 准备测试数据
        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction);

        // 执行测试
        transactionService.softDeleteTransaction(TRANSACTION_ID);

        // 验证结果
        assertTrue(transaction.getDeleted());
        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void softDeleteTransaction_NotFound() {
        // 准备测试数据
        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> 
            transactionService.softDeleteTransaction(TRANSACTION_ID)
        );
        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void softDeleteTransaction_AlreadyDeleted() {
        // 准备测试数据
        transaction.setDeleted(true);
        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Optional.of(transaction));

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> 
            transactionService.softDeleteTransaction(TRANSACTION_ID)
        );
        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
} 
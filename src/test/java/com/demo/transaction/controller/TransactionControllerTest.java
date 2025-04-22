package com.demo.transaction.controller;

import com.demo.transaction.dto.TransactionDTO;
import com.demo.transaction.dto.request.TransactionRequest;
import com.demo.transaction.dto.request.TransactionUpdateRequest;
import com.demo.transaction.dto.response.ApiResponse;
import com.demo.transaction.enumeration.TransactionType;
import com.demo.transaction.service.TransactionService;
import com.demo.transaction.strategy.TransactionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionHandler transactionHandler;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listTransactionsWithParameters_ShouldReturnTransactions() {
        // 准备测试数据
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(1L);
        transactionDTO.setTransactionNumber("TRX001");
        transactionDTO.setAccountId(1L);
        transactionDTO.setType(TransactionType.DEPOSIT);
        transactionDTO.setAmount(new BigDecimal("1000.00"));
        transactionDTO.setBalanceBefore(new BigDecimal("0.00"));
        transactionDTO.setBalanceAfter(new BigDecimal("1000.00"));
        transactionDTO.setTransactionTime(LocalDateTime.now());
        List<TransactionDTO> content = Arrays.asList(transactionDTO);

        // 创建Page mock
        Page<TransactionDTO> page = mock(Page.class);
        when(page.getContent()).thenReturn(content);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.getNumber()).thenReturn(0);
        when(page.getSize()).thenReturn(5);

        // 配置mock行为
        when(transactionService.getTransactionsByParameters(any(TransactionDTO.class), any(Pageable.class)))
                .thenReturn(page);

        // 执行测试
        ResponseEntity<ApiResponse<Page<TransactionDTO>>> response = transactionController.listTransactionsWithParameters(
                new TransactionDTO(), 0, 5, "id", "asc");

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals(ApiResponse.DEFAULT_SUCCESS_MESSAGE, response.getBody().getMessage());
        assertEquals(content, response.getBody().getData().getContent());
        assertEquals(1L, response.getBody().getData().getTotalElements());
    }

    @Test
    void getTransactionById_WhenTransactionExists_ShouldReturnTransaction() {
        // 准备测试数据
        Long transactionId = 1L;
        TransactionDTO expectedTransaction = new TransactionDTO();
        expectedTransaction.setId(transactionId);
        expectedTransaction.setTransactionNumber("TRX001");
        expectedTransaction.setAccountId(1L);
        expectedTransaction.setType(TransactionType.DEPOSIT);
        expectedTransaction.setAmount(new BigDecimal("1000.00"));
        expectedTransaction.setBalanceBefore(new BigDecimal("0.00"));
        expectedTransaction.setBalanceAfter(new BigDecimal("1000.00"));
        expectedTransaction.setTransactionTime(LocalDateTime.now());

        // 配置mock行为
        when(transactionService.getTransactionById(transactionId)).thenReturn(Optional.of(expectedTransaction));

        // 执行测试
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.getTransactionById(transactionId);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals(expectedTransaction, response.getBody().getData());
    }

    @Test
    void getTransactionById_WhenTransactionDoesNotExist_ShouldReturnNotFound() {
        // 准备测试数据
        Long transactionId = 1L;

        // 配置mock行为
        when(transactionService.getTransactionById(transactionId)).thenReturn(Optional.empty());

        // 执行测试
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.getTransactionById(transactionId);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.FAILURE, response.getBody().getCode());
        assertEquals("交易不存在", response.getBody().getMessage());
    }

    @Test
    void processTransaction_WhenSuccessful_ShouldReturnTransaction() {
        // 准备测试数据
        TransactionRequest request = new TransactionRequest();
        request.setType(TransactionType.DEPOSIT);
        request.setFromAccountId(1L);
        request.setAmount(new BigDecimal("1000.00"));
        request.setDescription("测试存款");

        TransactionDTO expectedTransaction = new TransactionDTO();
        expectedTransaction.setId(1L);
        expectedTransaction.setTransactionNumber("TRX001");
        expectedTransaction.setAccountId(1L);
        expectedTransaction.setType(TransactionType.DEPOSIT);
        expectedTransaction.setAmount(new BigDecimal("1000.00"));

        // 配置mock行为
        when(transactionHandler.handle(any(TransactionRequest.class))).thenReturn(expectedTransaction);

        // 执行测试
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.processTransaction(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals("Transaction Processed", response.getBody().getMessage());
        assertEquals(expectedTransaction, response.getBody().getData());
    }

    @Test
    void processTransaction_WhenFailed_ShouldReturnBadRequest() {
        // 准备测试数据
        TransactionRequest request = new TransactionRequest();
        request.setType(TransactionType.DEPOSIT);
        request.setFromAccountId(1L);
        request.setAmount(new BigDecimal("1000.00"));
        request.setDescription("测试存款");

        // 配置mock行为
        when(transactionHandler.handle(any(TransactionRequest.class)))
                .thenThrow(new IllegalArgumentException("余额不足"));

        // 执行测试
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.processTransaction(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.FAILURE, response.getBody().getCode());
        assertEquals("余额不足", response.getBody().getMessage());
    }

    @Test
    void listTransactionsWithParameters_ShouldHandlePaginationAndSorting() {
        // 准备测试数据
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setId(1L);
        transactionDTO.setTransactionNumber("TRX001");
        transactionDTO.setAccountId(1L);
        transactionDTO.setType(TransactionType.DEPOSIT);
        transactionDTO.setAmount(new BigDecimal("1000.00"));
        List<TransactionDTO> content = Arrays.asList(transactionDTO);

        // 创建Page mock
        Page<TransactionDTO> page = mock(Page.class);
        when(page.getContent()).thenReturn(content);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.getNumber()).thenReturn(1);
        when(page.getSize()).thenReturn(10);

        // 配置mock行为
        when(transactionService.getTransactionsByParameters(any(TransactionDTO.class), any(Pageable.class)))
                .thenReturn(page);

        // 执行测试 - 使用不同的分页和排序参数
        ResponseEntity<ApiResponse<Page<TransactionDTO>>> response = transactionController.listTransactionsWithParameters(
                new TransactionDTO(), 1, 10, "transactionNumber", "desc");

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals(content, response.getBody().getData().getContent());
        assertEquals(1L, response.getBody().getData().getTotalElements());

        // 验证service方法被调用时使用了正确的参数
        verify(transactionService).getTransactionsByParameters(
                any(TransactionDTO.class),
                argThat(pageable -> 
                    pageable.getPageNumber() == 1 &&
                    pageable.getPageSize() == 10 &&
                    pageable.getSort().getOrderFor("transactionNumber").getDirection() == Sort.Direction.DESC
                )
        );
    }

    @Test
    void updateTransaction_WhenSuccessful_ShouldReturnUpdatedTransaction() {
        // 准备测试数据
        Long transactionId = 1L;
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        request.setDescription("Updated description");
        request.setReferenceNumber("Updated REF");

        TransactionDTO expectedTransaction = new TransactionDTO();
        expectedTransaction.setId(transactionId);
        expectedTransaction.setDescription("Updated description");
        expectedTransaction.setReferenceNumber("Updated REF");

        // 配置mock行为
        when(transactionService.updateTransaction(eq(transactionId), any(TransactionUpdateRequest.class)))
                .thenReturn(expectedTransaction);

        // 执行测试
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.updateTransaction(transactionId, request);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals("Transaction Updated", response.getBody().getMessage());
        assertEquals(expectedTransaction, response.getBody().getData());
    }

    @Test
    void updateTransaction_WhenTransactionNotFound_ShouldReturnNotFound() {
        // 准备测试数据
        Long transactionId = 1L;
        TransactionUpdateRequest request = new TransactionUpdateRequest();

        // 配置mock行为
        when(transactionService.updateTransaction(eq(transactionId), any(TransactionUpdateRequest.class)))
                .thenThrow(new RuntimeException("Transaction not found with id: " + transactionId));

        // 执行测试
        ResponseEntity<ApiResponse<TransactionDTO>> response = transactionController.updateTransaction(transactionId, request);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.FAILURE, response.getBody().getCode());
        assertEquals("Transaction not found with id: " + transactionId, response.getBody().getMessage());
    }

    @Test
    void softDeleteTransaction_WhenSuccessful_ShouldReturnSuccess() {
        // 准备测试数据
        Long transactionId = 1L;

        // 配置mock行为
        doNothing().when(transactionService).softDeleteTransaction(transactionId);

        // 执行测试
        ResponseEntity<ApiResponse<Void>> response = transactionController.softDeleteTransaction(transactionId);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals("Transaction Deleted", response.getBody().getMessage());
    }

    @Test
    void softDeleteTransaction_WhenTransactionNotFound_ShouldReturnNotFound() {
        // 准备测试数据
        Long transactionId = 1L;

        // 配置mock行为
        doThrow(new RuntimeException("Transaction not found with id: " + transactionId))
                .when(transactionService).softDeleteTransaction(transactionId);

        // 执行测试
        ResponseEntity<ApiResponse<Void>> response = transactionController.softDeleteTransaction(transactionId);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.FAILURE, response.getBody().getCode());
        assertEquals("Transaction not found with id: " + transactionId, response.getBody().getMessage());
    }
} 
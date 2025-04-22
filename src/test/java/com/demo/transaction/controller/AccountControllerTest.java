package com.demo.transaction.controller;

import com.demo.transaction.dto.AccountDTO;
import com.demo.transaction.dto.response.ApiResponse;
import com.demo.transaction.service.AccountService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAccountsWithParameters_ShouldReturnAccounts() {
        // 准备测试数据
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setAccountNumber("ACC001");
        accountDTO.setUserId(1L);
        accountDTO.setBalance(new BigDecimal("1000.00"));
        accountDTO.setCurrency("CNY");
        List<AccountDTO> content = Arrays.asList(accountDTO);

        // 创建Page mock
        Page<AccountDTO> page = mock(Page.class);
        when(page.getContent()).thenReturn(content);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.getNumber()).thenReturn(0);
        when(page.getSize()).thenReturn(5);

        // 配置mock行为
        when(accountService.getAccountsByParameters(any(AccountDTO.class), any(Pageable.class)))
                .thenReturn(page);

        // 执行测试
        ResponseEntity<ApiResponse<Page<AccountDTO>>> response = accountController.listAccountsWithParameters(
                new AccountDTO(), 0, 5, "id", "asc");

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
    void getAccountById_WhenAccountExists_ShouldReturnAccount() {
        // 准备测试数据
        Long accountId = 1L;
        AccountDTO expectedAccount = new AccountDTO();
        expectedAccount.setId(accountId);
        expectedAccount.setAccountNumber("ACC001");
        expectedAccount.setUserId(1L);
        expectedAccount.setBalance(new BigDecimal("1000.00"));
        expectedAccount.setCurrency("CNY");

        // 配置mock行为
        when(accountService.getAccountById(accountId)).thenReturn(Optional.of(expectedAccount));

        // 执行测试
        ResponseEntity<ApiResponse<AccountDTO>> response = accountController.getAccountById(accountId);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals(expectedAccount, response.getBody().getData());
    }

    @Test
    void getAccountById_WhenAccountDoesNotExist_ShouldReturnNotFound() {
        // 准备测试数据
        Long accountId = 1L;

        // 配置mock行为
        when(accountService.getAccountById(accountId)).thenReturn(Optional.empty());

        // 执行测试
        ResponseEntity<ApiResponse<AccountDTO>> response = accountController.getAccountById(accountId);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.FAILURE, response.getBody().getCode());
        assertEquals("Account Not Found", response.getBody().getMessage());
    }

    @Test
    void listAccountsWithParameters_ShouldHandlePaginationAndSorting() {
        // 准备测试数据
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setAccountNumber("ACC001");
        accountDTO.setUserId(1L);
        accountDTO.setBalance(new BigDecimal("1000.00"));
        accountDTO.setCurrency("CNY");
        List<AccountDTO> content = Arrays.asList(accountDTO);

        // 创建Page mock
        Page<AccountDTO> page = mock(Page.class);
        when(page.getContent()).thenReturn(content);
        when(page.getTotalElements()).thenReturn(1L);
        when(page.getTotalPages()).thenReturn(1);
        when(page.getNumber()).thenReturn(1);
        when(page.getSize()).thenReturn(10);

        // 配置mock行为
        when(accountService.getAccountsByParameters(any(AccountDTO.class), any(Pageable.class)))
                .thenReturn(page);

        // 执行测试 - 使用不同的分页和排序参数
        ResponseEntity<ApiResponse<Page<AccountDTO>>> response = accountController.listAccountsWithParameters(
                new AccountDTO(), 1, 10, "accountNumber", "desc");

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals(content, response.getBody().getData().getContent());
        assertEquals(1L, response.getBody().getData().getTotalElements());

        // 验证service方法被调用时使用了正确的参数
        verify(accountService).getAccountsByParameters(
                any(AccountDTO.class),
                argThat(pageable -> 
                    pageable.getPageNumber() == 1 &&
                    pageable.getPageSize() == 10 &&
                    pageable.getSort().getOrderFor("accountNumber").getDirection() == Sort.Direction.DESC
                )
        );
    }
} 
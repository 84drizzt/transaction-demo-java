
package com.demo.transaction.service;

import com.demo.transaction.dto.AccountDTO;
import com.demo.transaction.entity.Account;
import com.demo.transaction.entity.User;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAccountById_ShouldReturnAccountDTO_WhenAccountExists() {
        // Arrange
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        User user = new User();
        user.setId(2L);
        account.setUser(user);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act
        Optional<AccountDTO> result = accountService.getAccountById(accountId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(accountId, result.get().getId());
        assertEquals(2L, result.get().getUserId());
    }

    @Test
    void getAccountById_ShouldReturnEmpty_WhenAccountNotExists() {
        // Arrange
        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act
        Optional<AccountDTO> result = accountService.getAccountById(accountId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getAccountsByParameters_ShouldReturnAccountPage() {
        // Arrange
        AccountDTO searchDTO = new AccountDTO();
        searchDTO.setUserId(1L);

        Account account = new Account();
        account.setId(1L);
        User user = new User();
        user.setId(1L);
        account.setUser(user);

        Page<Account> accountPage = new PageImpl<>(Collections.singletonList(account));
        when(accountRepository.findAll((Specification<Account>) any(), any(Pageable.class))).thenReturn(accountPage);

        // Act
        Page<AccountDTO> result = accountService.getAccountsByParameters(searchDTO, Pageable.unpaged());

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(1L, result.getContent().get(0).getUserId());
        assertEquals(1, result.getTotalElements());
    }
}

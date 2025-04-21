package com.demo.transaction.service;

import com.demo.transaction.dto.AccountDTO;
import com.demo.transaction.entity.Account;
import com.demo.transaction.enumeration.SearchOperation;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.SpecificationBuilder;
import com.demo.transaction.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<AccountDTO> getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(account -> {
                    AccountDTO accountDTO = new AccountDTO();
                    BeanUtils.copyProperties(account, accountDTO);
                    accountDTO.setUserId(account.getUser().getId());
                    return accountDTO;
                });
    }


    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByParameters(AccountDTO searchDTO, Pageable pageable) {

        SpecificationBuilder<Account> builder = new SpecificationBuilder<Account>()
                .with("user.id", searchDTO.getUserId(), SearchOperation.EQUAL)
                .with("accountNumber", searchDTO.getAccountNumber(), SearchOperation.EQUAL)
                .with("currency", searchDTO.getCurrency(), SearchOperation.EQUAL);

        Page<Account> accounts = accountRepository.findAll(builder.build(), pageable);
        return accounts.stream()
                .map(account -> {
                    AccountDTO accountDTO = new AccountDTO();
                    BeanUtils.copyProperties(account, accountDTO);
                    accountDTO.setUserId(account.getUser().getId());
                    return accountDTO;
                }).toList();
    }
} 
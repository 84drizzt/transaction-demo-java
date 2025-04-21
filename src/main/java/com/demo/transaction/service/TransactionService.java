package com.demo.transaction.service;

import com.demo.transaction.dto.AccountDTO;
import com.demo.transaction.dto.TransactionDTO;
import com.demo.transaction.entity.Account;
import com.demo.transaction.entity.Transaction;
import com.demo.transaction.entity.User;
import com.demo.transaction.enumeration.SearchOperation;
import com.demo.transaction.enumeration.TransactionType;
import com.demo.transaction.repository.AccountRepository;
import com.demo.transaction.repository.SpecificationBuilder;
import com.demo.transaction.repository.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Transactional(readOnly = true)
    public Optional<TransactionDTO> getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(this::convertToDTO);
    }


    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByParameters(TransactionDTO searchDTO, Pageable pageable) {

        SpecificationBuilder<Transaction> builder = new SpecificationBuilder<Transaction>()
                .with("account.id", searchDTO.getAccountId(), SearchOperation.EQUAL)
                .with("transactionNumber", searchDTO.getTransactionNumber(), SearchOperation.EQUAL)
                .with("account.id", searchDTO.getRelatedAccountId(), SearchOperation.EQUAL);

        Page<Transaction> transactions = transactionRepository.findAll(builder.build(), pageable);
        return transactions.stream()
                .map(this::convertToDTO)
                .toList();
    }


    public TransactionDTO deposit(Long accountId, BigDecimal amount, String description) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        
        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(balanceBefore.add(amount));
        accountRepository.save(account);
        
        Transaction transaction = new Transaction();
        transaction.setTransactionNumber(generateTransactionNumber());
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(account.getBalance());
        transaction.setDescription(description);
        
        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    public TransactionDTO withdraw(Long accountId, BigDecimal amount, String description) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(balanceBefore.subtract(amount));
        accountRepository.save(account);
        
        Transaction transaction = new Transaction();
        transaction.setTransactionNumber(generateTransactionNumber());
        transaction.setAccount(account);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(account.getBalance());
        transaction.setDescription(description);
        
        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }

    public TransactionDTO transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("From account not found with id: " + fromAccountId));
        
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("To account not found with id: " + toAccountId));
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new RuntimeException("Currency mismatch");
        }
        
        BigDecimal fromBalanceBefore = fromAccount.getBalance();
        fromAccount.setBalance(fromBalanceBefore.subtract(amount));
        accountRepository.save(fromAccount);
        
        BigDecimal toBalanceBefore = toAccount.getBalance();
        toAccount.setBalance(toBalanceBefore.add(amount));
        accountRepository.save(toAccount);
        
        Transaction transaction = new Transaction();
        transaction.setTransactionNumber(generateTransactionNumber());
        transaction.setAccount(fromAccount);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(fromBalanceBefore);
        transaction.setBalanceAfter(fromAccount.getBalance());
        transaction.setDescription(description);
        transaction.setRelatedAccount(toAccount);
        
        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }


    private String generateTransactionNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        BeanUtils.copyProperties(transaction, dto);
        dto.setAccountId(transaction.getAccount().getId());
        if (transaction.getRelatedAccount() != null) {
            dto.setRelatedAccountId(transaction.getRelatedAccount().getId());
        }
        return dto;
    }
} 
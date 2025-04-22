package com.demo.transaction.service;

import com.demo.transaction.dto.TransactionDTO;
import com.demo.transaction.dto.request.TransactionUpdateRequest;
import com.demo.transaction.entity.Account;
import com.demo.transaction.entity.Transaction;
import com.demo.transaction.enumeration.SearchOperation;
import com.demo.transaction.enumeration.TransactionType;
import com.demo.transaction.exception.IllegalTransactionException;
import com.demo.transaction.exception.TransactionDeletedException;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                .filter(transaction -> !transaction.getDeleted())
                .map(this::convertToDTO);
    }


    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByParameters(TransactionDTO searchDTO, Pageable pageable) {

        SpecificationBuilder<Transaction> builder = new SpecificationBuilder<Transaction>()
                .with("account.id", searchDTO.getAccountId(), SearchOperation.EQUAL)
                .with("transactionNumber", searchDTO.getTransactionNumber(), SearchOperation.EQUAL)
                .with("relatedAccount.id", searchDTO.getRelatedAccountId(), SearchOperation.EQUAL)
                .with("deleted", false, SearchOperation.EQUAL);

        Page<Transaction> transactions = transactionRepository.findAll(builder.build(), pageable);
        return transactions.stream()
                .map(this::convertToDTO)
                .toList();
    }


    public TransactionDTO deposit(Long accountId, BigDecimal amount, String description) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalTransactionException("Account not found with id: " + accountId));
        
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
                .orElseThrow(() -> new IllegalTransactionException("Account not found with id: " + accountId));
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalTransactionException("Insufficient balance");
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
                .orElseThrow(() -> new IllegalTransactionException("From account not found with id: " + fromAccountId));
        
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalTransactionException("To account not found with id: " + toAccountId));
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalTransactionException("Insufficient balance");
        }
        
        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new IllegalTransactionException("Currency mismatch");
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
    
    /**
     * 更新交易信息
     * @param id 交易ID
     * @param request 更新请求
     * @return 更新后的交易
     */
    public TransactionDTO updateTransaction(Long id, TransactionUpdateRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .filter(t -> !t.getDeleted())
                .orElseThrow(() -> new IllegalTransactionException("Transaction not found with id: " + id));
        
        // 只允许更新描述和参考号，不允许更新金额、账户等关键信息
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        
        if (request.getReferenceNumber() != null) {
            transaction.setReferenceNumber(request.getReferenceNumber());
        }
        
        transaction = transactionRepository.save(transaction);
        return convertToDTO(transaction);
    }
    
    /**
     * 软删除交易
     * @param id 交易ID
     */
    public void softDeleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalTransactionException("Transaction not found with id: " + id));

        if (transaction.getDeleted()) {
            throw new TransactionDeletedException("Transaction already deleted");
        }
        
        transaction.setDeleted(true);
        transactionRepository.save(transaction);
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
package com.demo.transaction.strategy;

import com.demo.transaction.dto.TransactionDTO;
import com.demo.transaction.dto.request.TransactionRequest;
import com.demo.transaction.enumeration.TransactionType;
import com.demo.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionHandlerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionHandler transactionHandler;

    @Test
    void handle_DepositTransaction_ShouldCallDepositService() {
        TransactionRequest request = new TransactionRequest();
        request.setType(TransactionType.DEPOSIT);
        request.setFromAccountId(1L);
        request.setAmount(BigDecimal.TEN);
        request.setDescription("Deposit test");

        TransactionDTO expected = new TransactionDTO();
        when(transactionService.deposit(anyLong(), any(), anyString())).thenReturn(expected);

        TransactionDTO result = transactionHandler.handle(request);

        assertSame(expected, result);
        verify(transactionService).deposit(1L, BigDecimal.TEN, "Deposit test");
    }

    @Test
    void handle_WithdrawTransaction_ShouldCallWithdrawService() {
        TransactionRequest request = new TransactionRequest();
        request.setType(TransactionType.WITHDRAW);
        request.setFromAccountId(2L);
        request.setAmount(BigDecimal.ONE);
        request.setDescription("Withdraw test");

        TransactionDTO expected = new TransactionDTO();
        when(transactionService.withdraw(anyLong(), any(), anyString())).thenReturn(expected);

        TransactionDTO result = transactionHandler.handle(request);

        assertSame(expected, result);
        verify(transactionService).withdraw(2L, BigDecimal.ONE, "Withdraw test");
    }

    @Test
    void handle_TransferTransaction_ShouldCallTransferService() {
        TransactionRequest request = new TransactionRequest();
        request.setType(TransactionType.TRANSFER);
        request.setFromAccountId(3L);
        request.setToAccountId(4L);
        request.setAmount(BigDecimal.valueOf(5));
        request.setDescription("Transfer test");

        TransactionDTO expected = new TransactionDTO();
        when(transactionService.transfer(anyLong(), anyLong(), any(), anyString())).thenReturn(expected);

        TransactionDTO result = transactionHandler.handle(request);

        assertSame(expected, result);
        verify(transactionService).transfer(3L, 4L, BigDecimal.valueOf(5), "Transfer test");
    }

    @Test
    void handle_UnsupportedTransactionType_ShouldThrowException() {
        TransactionRequest request = new TransactionRequest();
        request.setType(null);

        assertThrows(IllegalArgumentException.class, () -> transactionHandler.handle(request));
    }
}

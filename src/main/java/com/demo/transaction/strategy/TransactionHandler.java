package com.demo.transaction.strategy;

import com.demo.transaction.dto.TransactionDTO;
import com.demo.transaction.dto.request.TransactionRequest;
import com.demo.transaction.enumeration.TransactionType;
import com.demo.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.function.Function;

@Component
public class TransactionHandler {

    @Autowired
    TransactionService transactionService;

    private final Map<TransactionType, Function<TransactionRequest, TransactionDTO>> handlerMap = Map.of(
            TransactionType.DEPOSIT, req -> transactionService.deposit(
                    req.getFromAccountId(), req.getAmount(), req.getDescription()
            ),
            TransactionType.WITHDRAW, req -> transactionService.withdraw(
                    req.getFromAccountId(), req.getAmount(), req.getDescription()
            ),
            TransactionType.TRANSFER, req -> transactionService.transfer(
                    req.getFromAccountId(), req.getToAccountId(), req.getAmount(), req.getDescription()
            )
    );

    public TransactionDTO handle(TransactionRequest request) {
        Function<TransactionRequest, TransactionDTO> handler = handlerMap.get(request.getType());
        if (handler == null) {
            throw new IllegalArgumentException("Transaction Type Not Supported: " + request.getType());
        }
        return handler.apply(request);
    }
}

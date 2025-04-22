package com.demo.transaction.exception;

public class TransactionDeletedException extends RuntimeException{
    public TransactionDeletedException(String message) {
        super(message);
    }
}

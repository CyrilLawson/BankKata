package com.appsland.exception;

/**
 *
 * Custom class exception pour un solde insuffisant en cas de retrait
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}

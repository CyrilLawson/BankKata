package com.appsland.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerErrorException;

import java.time.Instant;

/**
 *
 * Exception handler pour retourner des messages personnalisés par rapport aux exceptions
 *
 */
@ControllerAdvice(basePackages ="com.appsland")
@Slf4j
public class OperationExceptionhandler {

    private static final Instant TIMESTAMP = Instant.now();

    @ExceptionHandler(AccountNotFoundException.class)
    ProblemDetail handleEntityNotFoundException(AccountNotFoundException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Entity not found");
        problemDetail.setProperty("timestamp", TIMESTAMP);
        problemDetail.setDetail(e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    ProblemDetail handleEntityNotFoundException(InsufficientBalanceException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        problemDetail.setTitle("Insufficient balance");
        problemDetail.setProperty("timestamp", TIMESTAMP);
        problemDetail.setDetail(e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(value = {BadRequestException.class})
    public ProblemDetail handleException(BadRequestException exception) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad Request");
        problemDetail.setProperty("timestamp", TIMESTAMP);
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(value = {ServerErrorException.class})
    public ProblemDetail handleException(ServerErrorException exception) {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", TIMESTAMP);
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
}

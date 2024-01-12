package com.appsland.api;

import com.appsland.domain.Account;
import com.appsland.domain.Operation;
import com.appsland.domain.OperationType;
import com.appsland.dto.OperationRequest;
import com.appsland.dto.OperationResponse;
import com.appsland.exception.AccountNotFoundException;
import com.appsland.exception.InsufficientBalanceException;
import com.appsland.service.AccountService;
import com.appsland.service.OperationService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * API pour effectuer des opérations de dépôt - retrait et de lister les opérations d'un compte
 */

@RestController
@RequestMapping(path = "/v1")
@AllArgsConstructor
@Slf4j
public class OperationController {

    private AccountService accountService;
    private OperationService operationService;

    @io.swagger.v3.oas.annotations.Operation(description = "Make a deposit or withdrawal on an account")
    @PostMapping(path = "/operations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value =
            {@ApiResponse(responseCode = "404", description = "Account not Found"),
                    @ApiResponse(responseCode = "201", description = "Operation created succesfully"),
                    @ApiResponse(responseCode = "404", description = "Operation not allowed")
            })
    public ResponseEntity execute(@RequestBody @Validated OperationRequest operationRequest,
                                  @RequestParam OperationType operationType) {

        if (operationRequest.operationAmount() <= 0) throw new IllegalArgumentException("Amount must > 0");

        Optional<Account> account = accountService.getAccount(operationRequest.accountNumber());

        if (account.isEmpty())
            throw new AccountNotFoundException(String.format("Account with number %d not found",
                    operationRequest.accountNumber()));

        var accountBalance = accountService.getBalance(LocalDate.now(), operationRequest.accountNumber());

        if (OperationType.WITHDRAWAL.equals(operationType) && accountBalance < operationRequest.operationAmount()) {
            throw new InsufficientBalanceException("Insufficient balance !!");
        }

        var operation = Operation.builder().amount(operationRequest.operationAmount()).operationType(operationType)
                .date(LocalDate.now()).account(account.get()).build();
        log.info("Executing operation {} of {} on account {}", operationType, operationRequest.operationAmount(), operationRequest.accountNumber());
        operationService.addOperation(operation);
        log.info("Operation {} of {} on account {} successfully executed", operationType, operationRequest.operationAmount(), operationRequest.accountNumber());

        return new ResponseEntity(new OperationResponse(String.format("Operation (%s) of %s on account %s",
                operationType, operationRequest.operationAmount(), operationRequest.accountNumber()),
                operationRequest.accountNumber()), HttpStatus.CREATED);
    }

    @GetMapping(path = "/operations/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @io.swagger.v3.oas.annotations.Operation(description = "Fetch all operations of an account")
    @ApiResponses(value =
            {@ApiResponse(responseCode = "404", description = "Account not Found"),
                    @ApiResponse(responseCode = "200", description = "Ok: Operation executed successfully")
            })
    public ResponseEntity fetchAllOperations(@PathVariable(value = "accountNumber") int accountNumber,
                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                             @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        Optional<Account> account = accountService.getAccount(accountNumber);

        if (account.isEmpty())
            throw new AccountNotFoundException(String.format("Account with number %d not found", accountNumber));

        var accountBalance = accountService.getBalance(LocalDate.now(), accountNumber);

        List<Operation> listOperations = operationService.getAllOperationForAccount(LocalDate.now(), accountNumber, page, size);

        var operationResponse = new OperationResponse(String.format("Balance on %s = %s for accountNumber : %s",
                LocalDate.now(),
                accountBalance, accountNumber), listOperations);

        return new ResponseEntity(operationResponse, HttpStatus.OK);

    }

}

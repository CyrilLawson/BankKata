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
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

import java.time.ZoneId;
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

    @ApiOperation(value = "Make a deposit or withdrawal on an account", response = OperationResponse.class)
    @PostMapping(path = "/operations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value =
            {@ApiResponse(code = 404, message = "Account not Found"),
                    @ApiResponse(code = 201, message = "Operation created succesfully"),
                    @ApiResponse(code = 404, message = "Operation not allowed")
            })
    public ResponseEntity execute(@RequestBody @Validated OperationRequest operationRequest,
                                  @RequestParam OperationType operationType) {

        if (operationRequest.getOperationAmount() <= 0) throw new IllegalArgumentException("Amount must > 0");

        Optional<Account> account = accountService.getAccount(operationRequest.getAccountNumber());

        if (!account.isPresent())
            throw new AccountNotFoundException(String.format("Account with number %d not found",
                    operationRequest.getAccountNumber()));

        Double accountBalance = accountService.getBalance(new Date(), operationRequest.getAccountNumber());

        if (OperationType.WITHDRAWAL.equals(operationType) && accountBalance < operationRequest.getOperationAmount()) {
            throw new InsufficientBalanceException("Insufficient balance !!");
        }

        Operation operation = new Operation();
        operation.setAmount(operationRequest.getOperationAmount());
        operation.setOperationType(operationType);
        operation.setAccount(account.get());
        log.info("Executing operation {} of {} on account {}", operationType, operationRequest.getOperationAmount(), operationRequest.getAccountNumber());
        operationService.addOperation(operation);
        log.info("Operation {} of {} on account {} successfully executed", operationType, operationRequest.getOperationAmount(), operationRequest.getAccountNumber());

        return new ResponseEntity(new OperationResponse(String.format("Operation (%s) of %s on account %s",
                operationType, operationRequest.getOperationAmount(), operationRequest.getAccountNumber())), HttpStatus.CREATED);
    }

    @GetMapping(path = "/operations/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Fetch all operations of an account", response = OperationResponse.class)
    @ApiResponses(value =
            {@ApiResponse(code = 404, message = "Account not Found"),
                    @ApiResponse(code = 200, message = "Ok: Operation executed successfully")
            })
    public ResponseEntity fetchAllOperations(@PathVariable(value = "accountNumber") int accountNumber,
                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                             @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        Optional<Account> account = accountService.getAccount(accountNumber);

        if (!account.isPresent())
            throw new AccountNotFoundException(String.format("Account with number %d not found", accountNumber));

        Double accountBalance = accountService.getBalance(new Date(), accountNumber);

        List<Operation> listOperations = operationService.getAllOperationForAccount(new Date(), accountNumber, page, size);

        OperationResponse operationResponse = new OperationResponse(String.format("Balance on %s = %s for accountNumber : %s",
                new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                accountBalance, accountNumber), listOperations);

        return new ResponseEntity(operationResponse, HttpStatus.OK);

    }

}

package com.appsland.service;

import com.appsland.domain.Account;
import com.appsland.domain.OperationType;
import com.appsland.repository.AccountRepository;
import com.appsland.repository.OperationRepository;
import lombok.AllArgsConstructor;
import com.appsland.domain.Operation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 *
 * Classe service où sera implémentée la logique métier d'un compte
 */
@Service
@AllArgsConstructor
public class AccountService {

    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    public Optional<Account> getAccount(int accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Double getBalance(LocalDate date, int accountNumber){

        double totalDeposit = operationRepository.findAllByDateLessThanEqualAndOperationTypeAndAccount_AccountNumber(
                date,
                OperationType.DEPOSIT, accountNumber)
                .stream().mapToDouble(Operation::getAmount).sum();

        double totalWithdrawal = operationRepository.findAllByDateLessThanEqualAndOperationTypeAndAccount_AccountNumber(
                date,
                OperationType.WITHDRAWAL, accountNumber)
                .stream().mapToDouble(Operation::getAmount).sum();

        return totalDeposit - totalWithdrawal;
    }
}

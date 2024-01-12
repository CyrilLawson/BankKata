package com.appsland.repository;

import com.appsland.domain.Operation;
import com.appsland.domain.OperationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * Classe repository d'une opération
 */
public interface OperationRepository extends JpaRepository<Operation, Integer> {

    //@Query("select t from Operation t where t.date<=:date and t.operationType=:operationType and t.account.accountNumber=:accountNumber")
    List<Operation> findAllByDateLessThanEqualAndOperationTypeAndAccount_AccountNumber(LocalDate date, OperationType operationType, int accountNumber);

    //@Query("select t from Operation t where t.date<=:date and t.account.accountNumber=:accountNumber")
    List<Operation> findAllByDateLessThanEqualAndAccount_AccountNumber(LocalDate date, int accountNumber, Pageable pageable);
}

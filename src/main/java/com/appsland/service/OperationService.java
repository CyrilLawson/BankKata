package com.appsland.service;

import com.appsland.domain.Operation;
import com.appsland.repository.OperationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 *
 * Classe service où sera implémentée la logique métier d'une opération
 */
@Service
@AllArgsConstructor
public class OperationService {

    private OperationRepository operationRepository;

    public Operation addOperation(Operation operation) {
        return operationRepository.save(operation);
    }

    public List<Operation> getAllOperationForAccount(Date date, int accountNumber, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"date"));
        return operationRepository.findAllByAccountNumber(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                , accountNumber, pageRequest);
    }
}

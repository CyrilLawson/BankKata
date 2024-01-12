package com.appsland.repository;

import com.appsland.domain.Account;
import com.appsland.domain.Customer;
import com.appsland.domain.Operation;
import com.appsland.domain.OperationType;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class OperationRepositoryShould {

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @Test
    public void returnAllDeposit(){
        Customer customer = Customer.builder()
                .email("test@gmail.com").age(12).firstName("test").lastName("test").build();
        customerRepository.save(customer);
        account = Account.builder().accountNumber(123).customer(customer).build();
        accountRepository.save(account);

        operationRepository.save( Operation.builder().operationType(OperationType.DEPOSIT)
                .amount(500d).date(LocalDate.now()).account(account).build());
        operationRepository.save( Operation.builder().operationType(OperationType.DEPOSIT)
                .amount(100d).date(LocalDate.now()).account(account).build());

        List<Operation> list = operationRepository.findAllByDateLessThanEqualAndOperationTypeAndAccount_AccountNumber(LocalDate.now(),
                OperationType.DEPOSIT, account.getAccountNumber());
        Assert.assertFalse(list.isEmpty());
        Assert.assertEquals(2, list.size());
        list.forEach(x -> Assertions.assertThat(x.getOperationType()).isEqualTo(OperationType.DEPOSIT));

    }

    @Test
    public void returnAllWithdrawal(){
        Customer customer = Customer.builder()
                .email("test@gmail.com").age(12).firstName("test").lastName("test").build();
        customerRepository.save(customer);
        account = Account.builder().customer(customer).accountNumber(123456).build();
        accountRepository.save(account);

        operationRepository.save( Operation.builder().operationType(OperationType.WITHDRAWAL)
                .date(LocalDate.now()).amount(50d).account(account).build());
        operationRepository.save( Operation.builder().operationType(OperationType.WITHDRAWAL)
                .date(LocalDate.now()).amount(100d).account(account).build());

        List<Operation> list = operationRepository.findAllByDateLessThanEqualAndOperationTypeAndAccount_AccountNumber(LocalDate.now(),
                OperationType.WITHDRAWAL, account.getAccountNumber());
        Assert.assertFalse(list.isEmpty());
        Assertions.assertThat(list).hasSize(2);
        list.forEach(x -> Assertions.assertThat(x.getOperationType()).isEqualTo(OperationType.WITHDRAWAL));

    }
}

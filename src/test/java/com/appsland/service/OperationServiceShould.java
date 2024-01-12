package com.appsland.service;

import com.appsland.BankKataApplication;
import com.appsland.domain.Account;
import com.appsland.domain.Customer;
import com.appsland.domain.Operation;
import com.appsland.domain.OperationType;
import com.appsland.repository.AccountRepository;
import com.appsland.repository.CustomerRepository;
import com.appsland.repository.OperationRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class OperationServiceShould {

    @Autowired
    private OperationService operationService;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected AccountRepository accountRepository;

    private Account account;

    @Before
    public void setUp() {
        Customer customer = Customer.builder()
                .email("test@gmail.com").age(12).firstName("test").lastName("test").build();
        customerRepository.save(customer);
        account = Account.builder().accountNumber(123).customer(customer).build();
        accountRepository.save(account);

        operationRepository.save( Operation.builder()
                .operationType(OperationType.DEPOSIT)
                .amount(500d).date(LocalDate.now()).account(account).build());
        operationRepository.save( Operation.builder()
                .operationType(OperationType.DEPOSIT)
                .amount(100d).date(LocalDate.now()).account(account).build());
        operationRepository.save( Operation.builder()
                .operationType(OperationType.WITHDRAWAL)
                .amount(250d).date(LocalDate.now()).account(account).build());
    }

    @Test
    public void return_all_operations() {
        List<Operation> list = operationService.getAllOperationForAccount(LocalDate.now(),
                account.getAccountNumber(), 0, 10);
        Assertions.assertThat(list).isNotEmpty();
        Map<OperationType, Long> counted = list.stream().map(Operation::getOperationType)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Assertions.assertThat(counted).containsEntry(OperationType.DEPOSIT, 2L);
        Assertions.assertThat(counted).containsEntry(OperationType.WITHDRAWAL, 1L);

    }
}

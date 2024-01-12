package com.appsland.service;

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

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class AccountServiceShould {

    @Autowired
    private AccountService accountService;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @Before
    public void setUp(){
        Customer customer = Customer.builder()
                .email("test@gmail.com").age(12).firstName("test").lastName("test").build();
        customerRepository.save(customer);
        account = Account.builder().accountNumber(123).customer(customer).build();
        accountRepository.save(account);

        operationRepository.save( Operation.builder().operationType(OperationType.DEPOSIT)
                .date(LocalDate.now()).amount(500d).account(account).build());
        operationRepository.save( Operation.builder().operationType(OperationType.DEPOSIT)
                .date(LocalDate.now()).amount(100d).account(account).build());
        operationRepository.save( Operation.builder().operationType(OperationType.WITHDRAWAL)
                .date(LocalDate.now()).amount(250d).account(account).build());
    }

    @Test
    public void return_account_balance(){
        Double balance = accountService.getBalance(LocalDate.now(), account.getAccountNumber());
        Assertions.assertThat(balance).isEqualTo(350d);
    }
}

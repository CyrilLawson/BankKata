package com.appsland.repository;

import com.appsland.domain.Account;
import com.appsland.domain.Customer;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class AccountRepositoryShould {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    protected Account account;

    @Before
    public void setUp() {
        Customer customer = Customer.builder()
                .email("test@gmail.com").age(12).firstName("test").lastName("test").build();
        customerRepository.save(customer);
        account = Account.builder().accountNumber(123).customer(customer).build();
        accountRepository.save(account);
    }

    @Test
    public void returnAccountWithAccountNumber() {
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(123);
        Assert.assertTrue(optionalAccount.isPresent());
        Assertions.assertThat(optionalAccount).as("returned account with number 1234").contains(account);
    }

}

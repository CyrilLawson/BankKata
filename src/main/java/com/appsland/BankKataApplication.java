package com.appsland;

import com.appsland.domain.Account;
import com.appsland.domain.Customer;
import com.appsland.repository.AccountRepository;
import com.appsland.repository.CustomerRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@SpringBootApplication
public class BankKataApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankKataApplication.class, args);
    }

    @Profile("!test")
    @Bean
    public ApplicationRunner initializer(AccountRepository accountRepository,
                                         CustomerRepository customerRepository) {
        return args -> {
            customerRepository.saveAll(Arrays.asList(
                     Customer.builder()
                            .email("test1@gmail.com").age(12).firstName("test1").lastName("test1").build()
                    , Customer.builder()
                    .email("test2@gmail.com").age(12).firstName("test2").lastName("test2").build()));

            accountRepository.saveAll(Arrays.asList(
                    Account.builder().accountNumber(1001).customer(customerRepository.findById(1).get()).build(),
                    Account.builder().accountNumber(1002).customer(customerRepository.findById(1).get()).build(),
                    Account.builder().accountNumber(1003).customer(customerRepository.findById(2).get()).build()));
        };
    }
}

package com.appsland.repository;

import com.appsland.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 * Classe repository d'un compte
 */
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByAccountNumber(Integer accountNumber);
}

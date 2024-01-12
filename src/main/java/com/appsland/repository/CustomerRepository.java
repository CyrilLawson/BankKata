package com.appsland.repository;

import com.appsland.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * Classe repository d'un client
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}

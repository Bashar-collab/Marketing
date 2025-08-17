package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Customer;
import com.custempmanag.marketing.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserId(Long id);
}

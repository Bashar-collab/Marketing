package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

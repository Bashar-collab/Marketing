package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}

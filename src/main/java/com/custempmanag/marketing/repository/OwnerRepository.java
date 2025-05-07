package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
}

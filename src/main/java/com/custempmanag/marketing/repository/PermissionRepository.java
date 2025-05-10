package com.custempmanag.marketing.repository;


import com.custempmanag.marketing.model.Permission;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends CrudRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);

    Set<Permission> findByCodeIn(Set<String> codes);
}

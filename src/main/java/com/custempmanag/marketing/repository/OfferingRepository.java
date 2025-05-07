package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.Offering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OfferingRepository extends JpaRepository<Offering, Long> {
    List<Offering> findByOwnerId(Long ownerId);

//    Optional<Offering> findById(long offeringId);
}

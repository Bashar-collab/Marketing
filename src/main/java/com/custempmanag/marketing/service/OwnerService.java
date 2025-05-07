package com.custempmanag.marketing.service;

import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Owner;
import com.custempmanag.marketing.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public Owner getUserById(Long ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ownerId));
    }

}

package com.custempmanag.marketing.EntityResolver.ProfileResolver;

import com.custempmanag.marketing.model.Owner;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.OwnerRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

@Component
public class OwnerResolver implements ProfileResolver {

    @Autowired
    private OwnerRepository ownerRepository;

    @Override
    public Long getId() {
        Owner owner = new Owner();
        return owner.getId();
    }

    @Override
    public Long createProfile(User user) {
        Owner owner = new Owner();
        return ownerRepository.save(owner).getId();
    }

    @Override
    public String getProfileType() {
        return "Owner";
    }

    @Override
    public Object resolve(Long profileId) {
        return ownerRepository.findById(profileId).orElse(null);
    }
}

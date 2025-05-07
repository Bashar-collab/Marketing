package com.custempmanag.marketing.EntityResolver.ProfileResolver;

import com.custempmanag.marketing.model.Admin;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminResolver implements ProfileResolver {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Long getId() {
        Admin admin = new Admin();
        return admin.getId();
    }

    @Override
    public Long createProfile(User user) {
        Admin admin = new Admin();
        return adminRepository.save(admin).getId();
    }

    @Override
    public String getProfileType() {
        return "Admin";
    }

    @Override
    public Object resolve(Long profileId) {
        return adminRepository.findById(profileId).orElse(null);
    }
}

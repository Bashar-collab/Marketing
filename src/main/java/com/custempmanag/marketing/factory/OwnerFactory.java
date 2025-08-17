package com.custempmanag.marketing.factory;

import com.custempmanag.marketing.model.Owner;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.request.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class OwnerFactory implements UserFactory{

    private final PasswordEncoder passwordEncoder;

    public OwnerFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String getProfileType()
    {
        return "owner";
    }

    @Override
    public User createUser(RegisterRequest registerRequest) {
//        Owner owner = new Owner();
//        owner.setUsername(registerRequest.getUsername());
//        owner.setPhoneNumber(registerRequest.getPhoneNumber());
//        owner.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return null;
    }


}

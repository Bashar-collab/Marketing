package com.custempmanag.marketing.factory;

import com.custempmanag.marketing.model.Customer;
import com.custempmanag.marketing.model.Owner;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.request.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomerFactory implements UserFactory {

    private final PasswordEncoder passwordEncoder;

    public CustomerFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String getProfileType() {
        return "customer";
    }

    @Override
    public User createUser(RegisterRequest registerRequest) {
//        Customer customer = new Customer();
//        customer.setUsername(registerRequest.getUsername());
//        customer.setPhoneNumber(registerRequest.getPhoneNumber());
//        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return null;
    }
}


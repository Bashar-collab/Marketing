package com.custempmanag.marketing.EntityResolver.ProfileResolver;

import com.custempmanag.marketing.model.Customer;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerResolver implements ProfileResolver {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Long getId() {
        Customer customer = new Customer();
        return customer.getId();
    }

    @Override
    public Long createProfile(User user) {
        Customer customer = new Customer();
        return customerRepository.save(customer).getId();
    }

    @Override
    public String getProfileType() {
        return "Customer";
    }

    @Override
    public Object resolve(Long profileId) {
        return customerRepository.findById(profileId).orElse(null);
    }
}

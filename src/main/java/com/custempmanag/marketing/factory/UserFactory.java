package com.custempmanag.marketing.factory;

import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.request.RegisterRequest;

public interface UserFactory {
    String getProfileType();
    User createUser(RegisterRequest registerRequest);
}

package com.custempmanag.marketing.EntityResolver.ProfileResolver;

import com.custempmanag.marketing.model.User;

public interface ProfileResolver {
    Long getId();
    Long createProfile(User user);
    String getProfileType(); // Method to return the entity type
    Object resolve(Long profileId); // Method to resolve the entity by ID
}
